"""Converte o JSON da API do Guarnicê no formato que o importador do backend espera."""

import hashlib
import json
import re
from datetime import date

VAZIOS = {"", "n/a", "na", "-", "--", "não informado", "nao informado"}

# Campo da API -> chave na ficha técnica
CAMPOS_FICHA = {
    "direcao": "direcao",
    "roteiro": "roteiro",
    "fotografia": "fotografia",
    "edicao": "edicao",
    "arte": "arte",
    "trilha": "trilhaSonora",
    "som": "desenhoDeSom",
    "elenco": "elenco",
    "produtora": "producao",
    "complementoFichaTecnica": "complemento",
}

RE_PARTE_DURACAO = re.compile(r"(\d+)\s*([a-z'\"]*)")


def texto(valor) -> str | None:
    """Tira espaços sobrando e trata "N/A" e afins como vazio."""
    if valor is None:
        return None
    limpo = re.sub(r"[ \t ]+", " ", str(valor)).strip()
    return None if limpo.lower() in VAZIOS else limpo


def duracao_em_segundos(valor: str | None) -> int | None:
    """Aceita os formatos que aparecem na API: "19min", "5min48seg", "52min43s", "30seg", "57s", "1h30min", "01:20:00"."""
    if not valor:
        return None
    t = valor.strip().lower()

    relogio = re.fullmatch(r"(\d{1,2}):(\d{2})(?::(\d{2}))?", t)
    if relogio:
        a, b, c = relogio.groups()
        return int(a) * 3600 + int(b) * 60 + int(c) if c else int(a) * 60 + int(b)

    partes = RE_PARTE_DURACAO.findall(t)
    if not partes:
        return None

    total = 0
    anterior = None
    for numero, unidade in partes:
        n = int(numero)
        if unidade.startswith("h"):
            fator = 3600
        elif unidade.startswith("m") or unidade == "'":
            fator = 60
        elif unidade.startswith("s") or unidade == '"':
            fator = 1
        else:
            # Número sem unidade: depois de horas são minutos, depois de minutos são segundos, sozinho são minutos
            fator = {3600: 60, 60: 1}.get(anterior, 60)
        total += n * fator
        anterior = fator
    return total


def ficha_tecnica(dados: dict) -> dict[str, str]:
    ficha = {}
    for campo, chave in CAMPOS_FICHA.items():
        valor = texto(dados.get(campo))
        if valor:
            ficha[chave] = valor

    # Mostra/categoria do festival em que o filme foi exibido, ex.: "CONCURSO GUARNICÊ DE VÍDEO (1996)"
    mostras = []
    for categoria in dados.get("categorias") or []:
        titulo = texto(categoria.get("titulo"))
        ano = (categoria.get("evento") or {}).get("ano")
        if titulo:
            mostras.append(f"{titulo} ({ano})" if ano else titulo)
    if mostras:
        ficha["mostra"] = "; ".join(dict.fromkeys(mostras))
    return ficha


def montar_item(dados: dict, url_video: str | None, url_poster: str | None) -> dict:
    ano = dados.get("ano")
    genero = texto(dados.get("genero"))
    publico = dados.get("statusPublicacao") == "PUBLICO" and dados.get("exibirEmCinemateca") is not False
    # Ano no futuro é registro de teste do UIRA (ex.: "Filme XYZ", 2999): fica guardado, mas inativo
    if isinstance(ano, int) and ano > date.today().year + 1:
        publico = False

    item = {
        "origem": "UIRA",
        "idExterno": str(dados["id"]),
        "tipo": "FILME",
        "titulo": texto(dados.get("titulo")) or f"Filme {dados['id']}",
        "sinopse": texto(dados.get("sinopse")),
        "urlPoster": url_poster,
        # Gênero vai como veio do UIRA; a padronização fica para depois
        "generos": [genero] if genero else [],
        "dataLancamento": f"{int(ano):04d}-01-01" if isinstance(ano, int) and ano > 0 else None,
        "duracaoSegundos": duracao_em_segundos(dados.get("duracao")),
        "urlVideo": url_video,
        "classificacaoIndicativa": texto(dados.get("classificacaoIndicativa")),
        "uf": texto(dados.get("uf")),
        "tags": texto(dados.get("tags")),
        "fichaTecnica": ficha_tecnica(dados),
        "status": "ATIVA" if publico else "INATIVA",
    }
    item["hash"] = hash_item(item)
    return item


def montar_item_card(id_filme: str, card: dict, url_poster: str | None) -> dict:
    """Filme que o UIRA lista mas a API não tem: só os dados do card. Sem página nem vídeo no UIRA, entra inativo."""
    genero = texto(card.get("genero"))
    item = {
        "origem": "UIRA",
        "idExterno": id_filme,
        "tipo": "FILME",
        "titulo": texto(card.get("titulo")) or f"Filme {id_filme}",
        "sinopse": texto(card.get("sinopse")),
        "urlPoster": url_poster,
        "generos": [genero] if genero else [],
        "dataLancamento": None,
        "duracaoSegundos": duracao_em_segundos(card.get("duracao")),
        "urlVideo": None,
        "classificacaoIndicativa": texto(card.get("classificacao")),
        "uf": None,
        "tags": None,
        "fichaTecnica": {},
        "status": "INATIVA",
    }
    item["hash"] = hash_item(item)
    return item


def arquivo_da_imagem(url: str | None) -> str | None:
    """Nome do arquivo na API do Guarnicê a partir da URL da imagem do card (ou None se for o placeholder)."""
    achado = re.search(r"dados-guarnice\.ufma\.br/api/imagens/arquivo/([^?\"]+)", url or "")
    return achado.group(1) if achado else None


def hash_item(item: dict) -> str:
    """Impressão digital do item: muda se qualquer campo mudar. O importador só regrava quando ela muda."""
    sem_hash = {k: v for k, v in item.items() if k != "hash"}
    canonico = json.dumps(sem_hash, ensure_ascii=False, sort_keys=True, separators=(",", ":"))
    return hashlib.sha256(canonico.encode("utf-8")).hexdigest()


def hash_dados(dados: dict) -> str:
    canonico = json.dumps(dados, ensure_ascii=False, sort_keys=True, separators=(",", ":"))
    return hashlib.sha256(canonico.encode("utf-8")).hexdigest()
