"""Requisições HTTP educadas: limite de requisições por segundo e novas tentativas com espera crescente."""

import asyncio
import random
import time

import httpx

USER_AGENT = "StreamingUFMA-Crawler/1.0 (projeto academico UFMA)"
TENTATIVAS = 5


class LimitadorTaxa:
    """Garante no máximo N requisições por segundo, mesmo com várias tarefas ao mesmo tempo."""

    def __init__(self, por_segundo: float):
        self._intervalo = 1.0 / por_segundo
        self._proximo = 0.0
        self._trava = asyncio.Lock()

    async def aguardar(self) -> None:
        # Reserva o próximo horário livre e dorme fora da trava, para as reservas seguirem em fila
        async with self._trava:
            agora = time.monotonic()
            inicio = max(agora, self._proximo)
            self._proximo = inicio + self._intervalo
        espera = inicio - agora
        if espera > 0:
            await asyncio.sleep(espera)

    def pausar(self, segundos: float) -> None:
        # Usado quando o servidor pede calma (429/5xx): todas as tarefas esperam
        self._proximo = max(self._proximo, time.monotonic() + segundos)


class ErroRequisicao(Exception):
    pass


def _espera_servidor(resposta: httpx.Response) -> float | None:
    valor = resposta.headers.get("retry-after", "")
    return float(valor) if valor.isdigit() else None


def _espera_backoff(tentativa: int) -> float:
    return min(60.0, 2.0**tentativa) + random.uniform(0, 1)


async def requisitar(
    cliente: httpx.AsyncClient,
    limitador: LimitadorTaxa,
    metodo: str,
    url: str,
    **kwargs,
) -> httpx.Response:
    """Faz a requisição respeitando o limitador. Tenta de novo em erro de rede, 429 e 5xx."""
    for tentativa in range(1, TENTATIVAS + 1):
        await limitador.aguardar()
        try:
            resposta = await cliente.request(metodo, url, **kwargs)
        except httpx.TransportError as erro:  # timeout, conexão recusada, DNS...
            motivo = f"{type(erro).__name__}"
            espera = _espera_backoff(tentativa)
        else:
            if resposta.status_code != 429 and resposta.status_code < 500:
                return resposta
            motivo = f"HTTP {resposta.status_code}"
            espera = _espera_servidor(resposta) or _espera_backoff(tentativa)
            limitador.pausar(espera)

        if tentativa == TENTATIVAS:
            raise ErroRequisicao(f"{metodo} {url} falhou após {TENTATIVAS} tentativas ({motivo})")
        print(f"  aviso: {motivo} em {url}; nova tentativa em {espera:.0f}s", flush=True)
        await asyncio.sleep(espera)

    raise AssertionError("inalcançável")


def novo_cliente() -> httpx.AsyncClient:
    return httpx.AsyncClient(
        headers={"User-Agent": USER_AGENT},
        timeout=httpx.Timeout(30.0, connect=15.0),
        limits=httpx.Limits(max_connections=10, max_keepalive_connections=10),
    )
