"""Acesso ao UIRA (WordPress): login com a senha da página, lista de IDs e link do vídeo de cada filme."""

import html
import re

import httpx

from .config import Config
from .rede import LimitadorTaxa, requisitar

RE_IDS = re.compile(r'data-todos-os-ids="([^"]*)"')
# Link "assistir" da página do filme (há outros para trailer e vídeo da direção)
RE_LINK_FILME = re.compile(
    r'class="[^"]*guarnice-sc-filme-link-video-filme[^"]*"[^>]*>\s*<a\s[^>]*href="([^"]+)"', re.S
)
RE_VIDEO_ID = re.compile(r"[?&]videoId=(\d+)")
URL_EMBED_EDUPLAY = "https://eduplay.rnp.br/app/video/embed/{}"

RE_NONCE = re.compile(r'data-nonce="([^"]+)"')
RE_ACAO_LOTE = re.compile(r'data-ajax-action-lote="([^"]+)"')
# Card do catálogo (vem do admin-ajax em lotes): <div class="guarnice-sc-categoria-item"><a href="...idFilme=123...">...</a></div>
RE_CARD = re.compile(r'<div\s+class="guarnice-sc-categoria-item"\s*>\s*<a href="[^"]*idFilme=(\d+)[^"]*">(.*?)</a>\s*</div>', re.S)
RE_CAMPO_CARD = {
    "imagem": re.compile(r'<img src="([^"]+)"'),
    "genero": re.compile(r'class="filme-genero">([^<]*)<'),
    "duracao": re.compile(r'guarnice-sc-filme-duracao"\s*><span>([^<]*)<'),
    "classificacao": re.compile(r'guarnice-sc-filme-classificacao-indicativa"\s*><span>([^<]*)<'),
    "titulo": re.compile(r'guarnice-sc-filme-titulo"\s*><span>([^<]*)<'),
    "sinopse": re.compile(r'guarnice-sc-filme-sinopse"\s*><span>(.*?)</span>', re.S),
}
IDS_POR_LOTE = 20


class UiraIndisponivel(Exception):
    pass


def link_valido(url: str | None) -> bool:
    return bool(url and re.match(r"https?://[^\s/;]+\.[^\s/;]+/", url))


class Uira:
    def __init__(self, config: Config, cliente: httpx.AsyncClient, limitador: LimitadorTaxa):
        self._config = config
        self._cliente = cliente
        self._limitador = limitador

    async def entrar(self) -> None:
        origem = self._config.origem_uira
        resposta = await requisitar(
            self._cliente,
            self._limitador,
            "POST",
            f"{origem}/wp-login.php?action=postpass",
            data={"post_password": self._config.password, "redirect_to": self._config.base_url},
        )
        if not any(nome.startswith("wp-postpass") for nome in self._cliente.cookies.keys()):
            raise UiraIndisponivel(f"login no UIRA falhou (HTTP {resposta.status_code}). Confira UIRA_PASSWORD.")

    async def _pagina(self, url: str) -> str:
        resposta = await requisitar(self._cliente, self._limitador, "GET", url, follow_redirects=True)
        if resposta.status_code != 200:
            raise UiraIndisponivel(f"{url} respondeu HTTP {resposta.status_code}")
        texto = resposta.text
        if 'name="post_password"' in texto:
            # A sessão expirou: entra de novo e repete uma vez
            await self.entrar()
            resposta = await requisitar(self._cliente, self._limitador, "GET", url, follow_redirects=True)
            texto = resposta.text
            if 'name="post_password"' in texto:
                raise UiraIndisponivel("o UIRA continua pedindo a senha depois do login")
        return texto

    async def listar_ids(self) -> list[str]:
        texto = await self._pagina(self._config.base_url)
        achado = RE_IDS.search(texto)
        if not achado:
            raise UiraIndisponivel("a página inicial do UIRA não trouxe a lista de filmes (catálogo fora do ar?)")
        # Guardados para pedir os cards do catálogo depois (ver cards())
        self._nonce = (RE_NONCE.search(texto) or [None, None])[1]
        self._acao_lote = (RE_ACAO_LOTE.search(texto) or [None, None])[1]
        ids = [i.strip() for i in achado.group(1).split(",") if i.strip().isdigit()]
        return list(dict.fromkeys(ids))  # sem repetidos, na ordem do site

    async def cards(self, ids: list[str]) -> dict[str, dict]:
        """Dados do card do catálogo (título, gênero, duração, sinopse, imagem). É o que o UIRA mostra na grade."""
        if not getattr(self, "_nonce", None) or not getattr(self, "_acao_lote", None):
            raise UiraIndisponivel("chame listar_ids() antes de cards()")
        cards = {}
        for inicio in range(0, len(ids), IDS_POR_LOTE):
            lote = ids[inicio : inicio + IDS_POR_LOTE]
            resposta = await requisitar(
                self._cliente,
                self._limitador,
                "POST",
                f"{self._config.origem_uira}/wp-admin/admin-ajax.php",
                data={"action": self._acao_lote, "nonce": self._nonce, "ids": ",".join(lote)},
            )
            corpo = resposta.json() if resposta.status_code == 200 else {}
            html_cards = (corpo.get("data") or {}).get("html", "") if corpo.get("success") else ""
            for id_filme, conteudo in RE_CARD.findall(html_cards):
                campos = {}
                for nome, regex in RE_CAMPO_CARD.items():
                    achado = regex.search(conteudo)
                    campos[nome] = html.unescape(re.sub(r"<[^>]+>", " ", achado.group(1))).strip() if achado else None
                cards[id_filme] = campos
        return cards

    async def link_video(self, id_filme: str) -> str | None:
        """URL para tocar o filme. Para vídeos do EduPlay, devolve o embed direto do player deles."""
        texto = await self._pagina(f"{self._config.origem_uira}/filme?idFilme={id_filme}&cinemateca=true")
        achado = RE_LINK_FILME.search(texto)
        if not achado:
            return None
        href = html.unescape(achado.group(1)).strip()
        video = RE_VIDEO_ID.search(href)
        if "dados-guarnice.ufma.br/public/player" in href and video:
            return URL_EMBED_EDUPLAY.format(video.group(1))
        # Alguns filmes têm o link quebrado no próprio UIRA (ex.: "uira.ufma.br;dados-guarnice..."): sem vídeo
        return href if link_valido(href) else None
