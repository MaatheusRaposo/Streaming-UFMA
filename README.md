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


## Testar a API com Swagger

Abra o Swagger UI e use `POST /usuario/register` para cadastrar uma conta, depois `POST /usuario/login` para obter o token de acesso. Clique em **Authorize** e informe o token para chamar as operações protegidas, identificadas pelo cadeado. O Swagger adiciona o prefixo `Bearer` automaticamente.

As rotas públicas de conteúdo podem ser chamadas sem autenticação, por exemplo `GET /contents`.

## Banco de dados

O Compose não inicia um PostgreSQL local: o backend conecta-se ao banco Supabase definido em `.env`. Na inicialização, Flyway executa as migrações pendentes e Hibernate valida o schema.
