"""Extração do catálogo do UIRA para data/catalogo.jsonl, com pôsteres no bucket.

Fluxo por filme: dados na API do Guarnicê -> link do vídeo na página do UIRA -> pôster para o bucket.
As requisições rodam em paralelo, mas cada servidor tem seu próprio limite de requisições por segundo.
O estado da última extração fica em data/estado.json: numa nova rodada, o crawler só visita a página
do UIRA e só reenvia o pôster dos filmes novos ou que mudaram.
"""

import asyncio
import json
import os
import time
from dataclasses import dataclass, field
from pathlib import Path

from .armazenamento import Armazenamento
from .config import DATA_DIR, Config, StorageConfig
from .guarnice import Guarnice
from .normalizar import arquivo_da_imagem, hash_dados, montar_item, montar_item_card
from .rede import LimitadorTaxa, novo_cliente
from .uira import Uira, UiraIndisponivel, link_valido

TAREFAS_PARALELAS = 8
SALVAR_ESTADO_A_CADA = 50


@dataclass
class Resumo:
    total: int = 0
    novos: int = 0
    alterados: int = 0
    iguais: int = 0
    sem_dados: list[str] = field(default_factory=list)
    so_card: list[str] = field(default_factory=list)
    sem_video: list[str] = field(default_factory=list)
    erros: dict[str, str] = field(default_factory=dict)


def _ler_json(caminho: Path, padrao):
    try:
        return json.loads(caminho.read_text(encoding="utf-8"))
    except FileNotFoundError:
        return padrao


def _gravar_atomico(caminho: Path, conteudo: str) -> None:
    # Grava num arquivo temporário e troca de uma vez: uma extração interrompida não corrompe o anterior
    temporario = caminho.with_suffix(caminho.suffix + ".tmp")
    temporario.write_text(conteudo, encoding="utf-8")
    os.replace(temporario, caminho)


class Extrator:
    def __init__(self, config: Config, storage: StorageConfig | None, completo: bool):
        self._config = config
        self._armazenamento = Armazenamento(storage) if storage else None
        self._completo = completo
        self._caminho_estado = DATA_DIR / "estado.json"
        self._estado: dict[str, dict] = _ler_json(self._caminho_estado, {}).get("filmes", {})
        self._itens: dict[str, dict] = {}
        self._resumo = Resumo()
        self._processados = 0
        self._inicio = 0.0

    async def executar(self, limite: int | None, ids_escolhidos: list[str] | None, forcar: bool) -> Resumo:
        DATA_DIR.mkdir(parents=True, exist_ok=True)
        self._inicio = time.monotonic()

        async with novo_cliente() as cliente_uira, novo_cliente() as cliente_guarnice:
            uira = Uira(self._config, cliente_uira, LimitadorTaxa(self._config.reqs_por_segundo_uira))
            guarnice = Guarnice(cliente_guarnice, LimitadorTaxa(self._config.reqs_por_segundo_guarnice))

            await uira.entrar()
            ids = await uira.listar_ids()
            print(f"UIRA: {len(ids)} filmes no catálogo", flush=True)
            self._conferir_quantidade(len(ids), forcar)

            if ids_escolhidos:
                ids = ids_escolhidos
            if limite:
                ids = ids[:limite]
            self._resumo.total = len(ids)

            fila: asyncio.Queue[str] = asyncio.Queue()
            for id_filme in ids:
                fila.put_nowait(id_filme)
            trabalhadores = [
                asyncio.create_task(self._trabalhador(fila, uira, guarnice))
                for _ in range(min(TAREFAS_PARALELAS, len(ids)))
            ]
            await fila.join()
            for tarefa in trabalhadores:
                tarefa.cancel()
            await asyncio.gather(*trabalhadores, return_exceptions=True)

            await self._completar_pelos_cards(uira, guarnice)

        parcial = bool(limite or ids_escolhidos)
        self._salvar_estado(None if parcial else len(ids))
        self._salvar_catalogo(ids, parcial)
        return self._resumo

    def _conferir_quantidade(self, quantidade: int, forcar: bool) -> None:
        # Proteção para quando o UIRA está em manutenção e devolve um catálogo vazio ou pela metade
        anterior = _ler_json(self._caminho_estado, {}).get("quantidadeNoUira")
        if quantidade == 0:
            raise UiraIndisponivel("o UIRA devolveu o catálogo vazio; nada foi alterado")
        if anterior and quantidade < anterior * 0.5 and not forcar:
            raise UiraIndisponivel(
                f"o UIRA listou {quantidade} filmes, menos da metade dos {anterior} da última vez. "
                "Pode estar em manutenção; use --forcar se a redução for real."
            )

    async def _trabalhador(self, fila: asyncio.Queue, uira: Uira, guarnice: Guarnice) -> None:
        while True:
            id_filme = await fila.get()
            try:
                await self._processar(id_filme, uira, guarnice)
            except Exception as erro:  # um filme com problema não derruba a extração
                self._resumo.erros[id_filme] = f"{type(erro).__name__}: {erro}"
                print(f"  erro no filme {id_filme}: {erro}", flush=True)
            finally:
                self._processados += 1
                self._mostrar_progresso()
                if self._processados % SALVAR_ESTADO_A_CADA == 0:
                    self._salvar_estado(None)
                fila.task_done()

    async def _processar(self, id_filme: str, uira: Uira, guarnice: Guarnice) -> None:
        dados = await guarnice.filme(id_filme)
        if dados is None:
            self._resumo.sem_dados.append(id_filme)
            return

        anterior = self._estado.get(id_filme, {})
        hash_api = hash_dados(dados)
        mudou_na_api = anterior.get("hashApi") != hash_api

        # Página do UIRA (mais lenta) só para filme novo, alterado ou sem vídeo da última vez
        if self._completo or mudou_na_api or not link_valido(anterior.get("urlVideo")):
            url_video = await uira.link_video(id_filme)
        else:
            url_video = anterior["urlVideo"]
        if not url_video:
            self._resumo.sem_video.append(id_filme)

        url_poster, chave_poster = await self._poster(id_filme, dados.get("imagemFile"), anterior, guarnice)

        item = montar_item(dados, url_video, url_poster)
        self._registrar(id_filme, item, anterior, {
            "hashApi": hash_api,
            "urlVideo": url_video,
            "imagemFile": dados.get("imagemFile"),
            "chavePoster": chave_poster,
            "urlPoster": url_poster,
        })

    async def _completar_pelos_cards(self, uira: Uira, guarnice: Guarnice) -> None:
        """Filmes que o UIRA lista mas a API não tem: salva o que o card do catálogo mostra (entram inativos)."""
        ids = list(self._resumo.sem_dados)
        if not ids:
            return
        cards = await uira.cards(ids)

        async def completar(id_filme: str) -> None:
            card = cards.get(id_filme)
            if not card:
                return
            try:
                anterior = self._estado.get(id_filme, {})
                arquivo = arquivo_da_imagem(card.get("imagem"))
                url_poster, chave_poster = await self._poster(id_filme, arquivo, anterior, guarnice)
                item = montar_item_card(id_filme, card, url_poster)
                self._registrar(id_filme, item, anterior, {
                    "hashApi": None,
                    "urlVideo": None,
                    "imagemFile": arquivo,
                    "chavePoster": chave_poster,
                    "urlPoster": url_poster,
                })
                self._resumo.so_card.append(id_filme)
            except Exception as erro:
                self._resumo.erros[id_filme] = f"{type(erro).__name__}: {erro}"

        await asyncio.gather(*(completar(i) for i in ids))
        self._resumo.sem_dados = [i for i in ids if i not in self._itens]

    def _registrar(self, id_filme: str, item: dict, anterior: dict, estado: dict) -> None:
        self._itens[id_filme] = item
        self._estado[id_filme] = {**estado, "hashItem": item["hash"]}
        if not anterior:
            self._resumo.novos += 1
        elif anterior.get("hashItem") != item["hash"]:
            self._resumo.alterados += 1
        else:
            self._resumo.iguais += 1

    async def _poster(
        self, id_filme: str, arquivo: str | None, anterior: dict, guarnice: Guarnice
    ) -> tuple[str | None, str | None]:
        """Devolve (URL pública, chave no bucket). Só baixa e envia de novo se o pôster mudou no UIRA."""
        if not arquivo:
            return None, None
        if not self._completo and anterior.get("imagemFile") == arquivo and anterior.get("urlPoster"):
            return anterior["urlPoster"], anterior.get("chavePoster")
        if not self._armazenamento:
            return None, None

        conteudo, tipo = await guarnice.imagem(arquivo)
        chave = Armazenamento.chave_poster(id_filme, arquivo, tipo)
        url = await self._armazenamento.enviar(chave, conteudo, tipo)

        chave_antiga = anterior.get("chavePoster")
        if chave_antiga and chave_antiga != chave:
            await self._armazenamento.apagar(chave_antiga)
        return url, chave

    def _mostrar_progresso(self) -> None:
        total = self._resumo.total
        if self._processados % 25 and self._processados != total:
            return
        decorrido = time.monotonic() - self._inicio
        restante = decorrido / self._processados * (total - self._processados) if self._processados else 0
        print(
            f"  {self._processados}/{total} filmes ({decorrido:.0f}s, faltam ~{restante:.0f}s)",
            flush=True,
        )

    def _salvar_estado(self, quantidade_no_uira: int | None) -> None:
        conteudo = _ler_json(self._caminho_estado, {})
        conteudo["filmes"] = self._estado
        if quantidade_no_uira is not None:
            conteudo["quantidadeNoUira"] = quantidade_no_uira
        _gravar_atomico(self._caminho_estado, json.dumps(conteudo, ensure_ascii=False, indent=1))

    def _salvar_catalogo(self, ids: list[str], parcial: bool) -> None:
        caminho = DATA_DIR / "catalogo.jsonl"
        itens: dict[str, dict] = {}
        if parcial and caminho.exists():
            # Rodada parcial (--limit/--ids): atualiza o catálogo anterior em vez de substituí-lo
            for linha in caminho.read_text(encoding="utf-8").splitlines():
                if linha.strip():
                    item = json.loads(linha)
                    itens[item["idExterno"]] = item
            ids = list(itens) + [i for i in ids if i not in itens]
        itens.update(self._itens)
        linhas = [json.dumps(itens[i], ensure_ascii=False) for i in ids if i in itens]
        _gravar_atomico(caminho, "\n".join(linhas) + ("\n" if linhas else ""))
        print(f"catálogo salvo em {caminho} ({len(linhas)} filmes)", flush=True)


async def extrair(
    config: Config,
    storage: StorageConfig | None,
    limite: int | None = None,
    ids: list[str] | None = None,
    completo: bool = False,
    forcar: bool = False,
) -> Resumo:
    return await Extrator(config, storage, completo).executar(limite, ids, forcar)
