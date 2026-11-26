"""Crawler do UIRA.

  python -m uira_crawler                     verifica o acesso (login + quantos filmes há). É o que roda no `docker compose up`.
  python -m uira_crawler extrair             extrai o catálogo para data/catalogo.jsonl e envia os pôsteres ao bucket
  python -m uira_crawler extrair --limit 20  só os 20 primeiros (para testar)
"""

import argparse
import asyncio
import sys

from .config import ConfigInvalida, carregar_config, carregar_storage
from .extrair import extrair
from .rede import ErroRequisicao, LimitadorTaxa, novo_cliente
from .uira import Uira, UiraIndisponivel


async def verificar(config) -> int:
    async with novo_cliente() as cliente:
        uira = Uira(config, cliente, LimitadorTaxa(config.reqs_por_segundo_uira))
        await uira.entrar()
        ids = await uira.listar_ids()
    print(f"UIRA ok: login feito e {len(ids)} filmes no catálogo.")
    print("Para extrair: docker compose run --rm crawler extrair")
    return 0


def _argumentos() -> argparse.Namespace:
    parser = argparse.ArgumentParser(prog="uira_crawler", description="Crawler do catálogo do UIRA")
    comandos = parser.add_subparsers(dest="comando")

    comandos.add_parser("verificar", help="testa o login e conta os filmes (padrão)")

    p = comandos.add_parser("extrair", help="extrai o catálogo para data/catalogo.jsonl")
    p.add_argument("--limit", type=int, help="processa só os N primeiros filmes")
    p.add_argument("--ids", help="processa só estes IDs, separados por vírgula")
    p.add_argument("--completo", action="store_true", help="ignora o estado anterior e baixa tudo de novo")
    p.add_argument("--forcar", action="store_true", help="aceita um catálogo bem menor que o da última vez")
    p.add_argument("--sem-posters", action="store_true", help="não envia pôsteres ao bucket")
    return parser.parse_args()


def main() -> int:
    args = _argumentos()
    try:
        config = carregar_config()
        if args.comando != "extrair":
            return asyncio.run(verificar(config))

        storage = None if args.sem_posters else carregar_storage()
        ids = [i.strip() for i in args.ids.split(",") if i.strip()] if args.ids else None
        resumo = asyncio.run(
            extrair(config, storage, limite=args.limit, ids=ids, completo=args.completo, forcar=args.forcar)
        )
    except ConfigInvalida as erro:
        print(f"Configuração inválida: {erro}", file=sys.stderr)
        return 1
    except (UiraIndisponivel, ErroRequisicao) as erro:
        print(f"UIRA indisponível: {erro}", file=sys.stderr)
        return 1
    except KeyboardInterrupt:
        print("\nInterrompido. O estado salvo até aqui é aproveitado na próxima rodada.", file=sys.stderr)
        return 130

    print(
        f"\nResumo: {resumo.total} filmes | {resumo.novos} novos | {resumo.alterados} alterados | "
        f"{resumo.iguais} iguais | {len(resumo.so_card)} só com dados do card (inativos) | "
        f"{len(resumo.sem_dados)} sem dados | {len(resumo.sem_video)} sem vídeo | {len(resumo.erros)} com erro"
    )
    if resumo.so_card:
        print(f"Sem página no UIRA, salvos só com o card: {', '.join(resumo.so_card)}")
    if resumo.sem_dados:
        print(f"Sem dados nenhum: {', '.join(resumo.sem_dados)}")
    if resumo.sem_video:
        print(f"Sem vídeo: {', '.join(resumo.sem_video)}")
    for id_filme, erro in resumo.erros.items():
        print(f"Erro em {id_filme}: {erro}")
    return 0 if not resumo.erros else 2


if __name__ == "__main__":
    sys.exit(main())
