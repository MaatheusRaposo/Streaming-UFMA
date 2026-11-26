import os
from dataclasses import dataclass, field
from pathlib import Path
from urllib.parse import urlparse

from dotenv import load_dotenv

# .env da raiz do repositório. No Docker ele não existe: o Compose já passa as variáveis.
ENV_FILE = Path(__file__).resolve().parents[2] / ".env"

# Onde ficam o catálogo extraído e o estado da última extração. No Docker, é o volume ./crawler/data.
DATA_DIR = Path(os.environ.get("CRAWLER_DATA_DIR", Path(__file__).resolve().parents[1] / "data"))

OBRIGATORIAS = ("UIRA_BASE_URL", "UIRA_PASSWORD")
OBRIGATORIAS_STORAGE = (
    "STORAGE_S3_ENDPOINT",
    "STORAGE_S3_REGION",
    "STORAGE_S3_ACCESS_KEY",
    "STORAGE_S3_SECRET_KEY",
    "STORAGE_BUCKET",
)


class ConfigInvalida(Exception):
    pass


@dataclass(frozen=True)
class StorageConfig:
    endpoint: str
    region: str
    bucket: str
    url_publica: str
    access_key: str = field(repr=False)
    secret_key: str = field(repr=False)


@dataclass(frozen=True)
class Config:
    base_url: str
    password: str = field(repr=False)
    # Requisições por segundo, para não sobrecarregar os servidores da UFMA
    reqs_por_segundo_uira: float = 2.0
    reqs_por_segundo_guarnice: float = 4.0

    @property
    def origem_uira(self) -> str:
        partes = urlparse(self.base_url)
        return f"{partes.scheme}://{partes.netloc}"


def _carregar_env() -> None:
    # utf-8-sig ignora o BOM do .env; sem interpolação, um $ na senha é lido como está
    load_dotenv(ENV_FILE, encoding="utf-8-sig", interpolate=False)


def _numero_positivo(nome: str, padrao: float) -> float:
    valor = os.environ.get(nome, "").strip()
    if not valor:
        return padrao
    try:
        numero = float(valor)
    except ValueError:
        raise ConfigInvalida(f"{nome} deve ser um número, recebido: {valor!r}") from None
    if numero <= 0:
        raise ConfigInvalida(f"{nome} deve ser maior que zero")
    return numero


def carregar_config() -> Config:
    _carregar_env()

    faltando = [nome for nome in OBRIGATORIAS if not os.environ.get(nome)]
    if faltando:
        raise ConfigInvalida(f"defina {', '.join(faltando)} no .env (veja o .env.example)")

    base_url = os.environ["UIRA_BASE_URL"].strip().rstrip("/")
    partes = urlparse(base_url)
    if partes.scheme not in ("http", "https") or not partes.netloc:
        raise ConfigInvalida(f"UIRA_BASE_URL deve ser uma URL completa (https://...), recebido: {base_url!r}")

    return Config(
        base_url=base_url,
        password=os.environ["UIRA_PASSWORD"],
        reqs_por_segundo_uira=_numero_positivo("CRAWLER_REQS_POR_SEGUNDO_UIRA", 2.0),
        reqs_por_segundo_guarnice=_numero_positivo("CRAWLER_REQS_POR_SEGUNDO_GUARNICE", 4.0),
    )


def carregar_storage() -> StorageConfig:
    _carregar_env()

    faltando = [nome for nome in OBRIGATORIAS_STORAGE if not os.environ.get(nome)]
    if faltando:
        raise ConfigInvalida(f"defina {', '.join(faltando)} no .env para enviar os pôsteres (veja o .env.example)")

    endpoint = os.environ["STORAGE_S3_ENDPOINT"].strip().rstrip("/")
    bucket = os.environ["STORAGE_BUCKET"].strip()

    # No Supabase, o endpoint S3 é https://<projeto>.storage.supabase.co/storage/v1/s3
    # e os arquivos públicos ficam em https://<projeto>.supabase.co/storage/v1/object/public/<bucket>/...
    url_publica = os.environ.get("STORAGE_PUBLIC_URL", "").strip().rstrip("/")
    if not url_publica:
        host = urlparse(endpoint).netloc
        if not host.endswith(".supabase.co"):
            raise ConfigInvalida("defina STORAGE_PUBLIC_URL: não foi possível deduzir a URL pública do endpoint")
        projeto = host.split(".")[0]
        url_publica = f"https://{projeto}.supabase.co/storage/v1/object/public/{bucket}"

    return StorageConfig(
        endpoint=endpoint,
        region=os.environ["STORAGE_S3_REGION"].strip(),
        bucket=bucket,
        url_publica=url_publica,
        access_key=os.environ["STORAGE_S3_ACCESS_KEY"].strip(),
        secret_key=os.environ["STORAGE_S3_SECRET_KEY"],
    )
