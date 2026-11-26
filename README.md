# Streaming UFMA

Projeto acadêmico de streaming de conteúdos culturais da UFMA. O repositório contém uma API em Spring Boot e uma aplicação web em Next.js. O backend usa PostgreSQL hospedado no Supabase.


## Configuração local

Na raiz do projeto, crie seu arquivo de ambiente a partir do exemplo:

```bash
cp .env.example .env
```

Edite `.env` e configure:

- `DATABASE_URL`
- `DATABASE_USERNAME` 
- `DATABASE_PASSWORD`
- `JWT_SECRET`

Use o `.env.example` como base e altere: `DATABASE_PASSWORD` e `JWT_SECRET`

## Iniciar em desenvolvimento

Na raiz do repositório:

```bash
docker compose up --build --watch
```

O Compose constrói e inicia o backend e o frontend. Mantenha o comando rodando. O Compose Watch sincroniza mudanças no código; alterações no `pom.xml` ou nas dependências do frontend reconstruirão o serviço correspondente.

- Frontend: <http://localhost:3000>
- API: <http://localhost:8080>
- Swagger UI: <http://localhost:8080/swagger-ui/index.html>
- OpenAPI JSON: <http://localhost:8080/v3/api-docs>


## Login no site

O site exige login: quem não entrou é levado para `/login` (e, depois de entrar, volta para a página que tentou abrir). A conta é criada em `/cadastro`. A sessão fica em dois cookies HttpOnly: o `accessToken` vale 15 minutos e o `refreshToken` 7 dias. Quando o primeiro vence, o `frontend/proxy.ts` pede um novo ao backend (`POST /usuario/refresh`) sem pedir a senha de novo. O botão **Sair** fica no topo do catálogo.

## Testar a API com Swagger

Abra o Swagger UI e use `POST /usuario/register` para cadastrar uma conta, depois `POST /usuario/login` para obter o token de acesso. Clique em **Authorize** e informe o token para chamar as operações protegidas, identificadas pelo cadeado. O Swagger adiciona o prefixo `Bearer` automaticamente.

As rotas públicas de conteúdo podem ser chamadas sem autenticação, por exemplo `GET /contents`.

## Banco de dados

O Compose não inicia um PostgreSQL local: o backend conecta-se ao banco Supabase definido em `.env`. Na inicialização, Flyway executa as migrações pendentes e Hibernate valida o schema.

## Catálogo do UIRA

Os filmes vêm do [UIRA](https://uira.ufma.br), a plataforma de streaming atual da UFMA. Guardamos no nosso banco só os **dados** de cada filme (título, sinopse, ano, gênero, duração, ficha técnica…). O vídeo continua no EduPlay (RNP) e toca no player deles, embutido na página `/assistir`. Os pôsteres ficam no Supabase Storage (bucket `posters`).

Variáveis no `.env` (veja o `.env.example`):

- `UIRA_BASE_URL` e `UIRA_PASSWORD`: acesso ao UIRA (só senha)
- `STORAGE_S3_ENDPOINT`, `STORAGE_S3_REGION`, `STORAGE_S3_ACCESS_KEY`, `STORAGE_S3_SECRET_KEY` e `STORAGE_BUCKET`: Supabase Storage, para os pôsteres. As chaves saem de *Storage → Settings → S3 Access Keys*.

Para puxar o catálogo e gravar no banco:

```bash
./sincronizar-uira.sh             # catálogo inteiro (~8 min na primeira vez)
./sincronizar-uira.sh --limit 20  # só os 20 primeiros, para testar
```

O script roda duas etapas, que também podem ser chamadas separadas:

1. `docker compose run --rm crawler extrair`: lê o UIRA e a API pública do Guarnicê e grava `crawler/data/catalogo.jsonl`. Envia os pôsteres ao bucket. Faz no máximo 2 requisições por segundo ao UIRA e 4 à API, e tenta de novo com espera crescente se o servidor reclamar.
2. `docker compose run --rm importador`: lê o `catalogo.jsonl` e cria ou atualiza os filmes no banco.

Rodar de novo é seguro e rápido. O crawler só visita de novo a página e só reenvia o pôster dos filmes que mudaram, e o importador só regrava os filmes cujo conteúdo mudou. Nada é apagado: se o UIRA sair do ar ou tirar um filme, ele continua no nosso banco. Se o UIRA devolver um catálogo vazio ou bem menor que o da última vez, o crawler para sem alterar nada.

No `docker compose up`, o serviço `crawler` só confere o acesso (login e quantidade de filmes) e termina. Ele não faz a extração completa.
