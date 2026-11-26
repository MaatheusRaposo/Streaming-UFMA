"""Envio dos pôsteres para o bucket (Supabase Storage, via protocolo S3)."""

import asyncio
import hashlib
from pathlib import PurePosixPath

import boto3
from botocore.config import Config as BotoConfig

from .config import StorageConfig

EXTENSOES = {"image/jpeg": ".jpg", "image/png": ".png", "image/webp": ".webp", "image/gif": ".gif"}


class Armazenamento:
    def __init__(self, config: StorageConfig):
        self._config = config
        # O cliente do boto3 é síncrono e pode ser usado por várias threads ao mesmo tempo
        self._s3 = boto3.client(
            "s3",
            endpoint_url=config.endpoint,
            region_name=config.region,
            aws_access_key_id=config.access_key,
            aws_secret_access_key=config.secret_key,
            config=BotoConfig(s3={"addressing_style": "path"}, retries={"max_attempts": 5, "mode": "standard"}),
        )

    @staticmethod
    def chave_poster(id_filme: str, arquivo_origem: str, tipo: str) -> str:
        # O hash do nome original entra na chave: se o UIRA trocar o pôster, a URL muda e o navegador não usa o antigo
        extensao = EXTENSOES.get(tipo) or PurePosixPath(arquivo_origem).suffix.lower() or ".jpg"
        versao = hashlib.sha1(arquivo_origem.encode("utf-8")).hexdigest()[:10]
        return f"uira/{id_filme}-{versao}{extensao}"

    def url_publica(self, chave: str) -> str:
        return f"{self._config.url_publica}/{chave}"

    async def enviar(self, chave: str, conteudo: bytes, tipo: str) -> str:
        await asyncio.to_thread(
            self._s3.put_object,
            Bucket=self._config.bucket,
            Key=chave,
            Body=conteudo,
            ContentType=tipo,
            CacheControl="public, max-age=31536000, immutable",
        )
        return self.url_publica(chave)

    async def apagar(self, chave: str) -> None:
        await asyncio.to_thread(self._s3.delete_object, Bucket=self._config.bucket, Key=chave)
