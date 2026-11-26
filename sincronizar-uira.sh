#!/usr/bin/env bash
# Puxa o catálogo do UIRA e grava no banco. Só baixa de novo o que mudou desde a última vez.
#   ./sincronizar-uira.sh             catálogo inteiro
#   ./sincronizar-uira.sh --limit 20  só os 20 primeiros (teste)
set -euo pipefail
cd "$(dirname "$0")"

docker compose run --rm --build crawler extrair "$@"
docker compose run --rm --build importador
