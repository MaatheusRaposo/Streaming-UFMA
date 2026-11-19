import os
from dataclasses import dataclass, field
from pathlib import Path
from urllib.parse import urlparse

from dotenv import load_dotenv

# .env da raiz do repositório. No Docker ele não existe: o Compose já passa as variáveis.
ENV_FILE = Path(__file__).resolve().parents[2] / ".env"

OBRIGATORIAS = ("UIRA_BASE_URL", "UIRA_USERNAME", "UIRA_PASSWORD")


class ConfigInvalida(Exception):
    pass


@dataclass(frozen=True)
class Config:
    base_url: str
    username: str = field(repr=False)
    password: str = field(repr=False)


def carregar_config() -> Config:
    # utf-8-sig ignora o BOM do .env; sem interpolação, um $ na senha é lido como está
    load_dotenv(ENV_FILE, encoding="utf-8-sig", interpolate=False)

    faltando = [nome for nome in OBRIGATORIAS if not os.environ.get(nome)]
    if faltando:
        raise ConfigInvalida(f"defina {', '.join(faltando)} no .env (veja o .env.example)")

    base_url = os.environ["UIRA_BASE_URL"].strip().rstrip("/")
    partes = urlparse(base_url)
    if partes.scheme not in ("http", "https") or not partes.netloc:
        raise ConfigInvalida(f"UIRA_BASE_URL deve ser uma URL completa (https://...), recebido: {base_url!r}")

    return Config(
        base_url=base_url,
        username=os.environ["UIRA_USERNAME"].strip(),
        password=os.environ["UIRA_PASSWORD"],
    )
