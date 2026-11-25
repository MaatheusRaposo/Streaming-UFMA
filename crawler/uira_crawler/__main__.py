import sys

import httpx

from .config import ConfigInvalida, carregar_config


def main() -> int:
    try:
        config = carregar_config()
    except ConfigInvalida as erro:
        print(f"Configuração inválida: {erro}", file=sys.stderr)
        return 1

    print(f"UIRA_BASE_URL: {config.base_url}")
    print("UIRA_PASSWORD: definida")

    # Por enquanto só testa se o UIRA responde; login e extração vêm depois da descoberta.
    try:
        resposta = httpx.get(config.base_url, follow_redirects=True, timeout=15)
    except (httpx.HTTPError, httpx.InvalidURL) as erro:
        print(f"Não foi possível acessar o UIRA: {erro}", file=sys.stderr)
        return 1

    print(f"UIRA respondeu {resposta.status_code} ({resposta.url})")
    return 0 if resposta.is_success else 1


if __name__ == "__main__":
    sys.exit(main())
