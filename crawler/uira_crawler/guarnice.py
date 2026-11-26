"""API pública do Guarnicê (dados-guarnice.ufma.br), de onde o UIRA tira os dados dos filmes."""

import httpx

from .rede import LimitadorTaxa, requisitar

API = "https://dados-guarnice.ufma.br/api"


class Guarnice:
    def __init__(self, cliente: httpx.AsyncClient, limitador: LimitadorTaxa):
        self._cliente = cliente
        self._limitador = limitador

    async def filme(self, id_filme: str) -> dict | None:
        """Dados do filme, ou None se a API não conhece esse ID."""
        resposta = await requisitar(self._cliente, self._limitador, "GET", f"{API}/filmes/{id_filme}")
        if resposta.status_code == 404:
            return None
        resposta.raise_for_status()
        return resposta.json()

    async def imagem(self, arquivo: str) -> tuple[bytes, str]:
        resposta = await requisitar(self._cliente, self._limitador, "GET", f"{API}/imagens/arquivo/{arquivo}")
        resposta.raise_for_status()
        tipo = resposta.headers.get("content-type", "image/jpeg").split(";")[0].strip()
        if not tipo.startswith("image/"):
            raise ValueError(f"imagem {arquivo} veio como {tipo}")
        return resposta.content, tipo
