import type { CredenciaisLogin, DadosCadastro, Usuario } from "@/types/usuario"

/**
 * Camada de autenticação no cliente.
 *
 * Comunica-se com as rotas `/api/auth/*` do Next.js, que encaminham as
 * requisições para a API Spring Boot usando `API_URL` no servidor.
 */

async function enviarRequisicaoAuth<T>(
  url: string,
  corpo: unknown,
  erroPadrao: string,
): Promise<T> {
  let resposta: Response

  try {
    resposta = await fetch(url, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(corpo),
    })
  } catch {
    throw new Error("Não foi possível conectar ao servidor. Tente novamente.")
  }

  const dados = (await resposta.json().catch(() => null)) as
    | ({ erro?: string } & Partial<T>)
    | null

  if (!resposta.ok) {
    throw new Error(dados?.erro || erroPadrao)
  }

  if (!dados) {
    throw new Error(erroPadrao)
  }

  return dados as T
}

export async function fazerLogin(
  credenciais: CredenciaisLogin,
): Promise<Usuario> {
  const dados = await enviarRequisicaoAuth<{ user: Usuario }>(
    "/api/auth/login",
    {
      email: credenciais.email.trim(),
      senha: credenciais.senha,
    },
    "E-mail ou senha incorretos.",
  )

  return dados.user
}

export async function cadastrarUsuario(
  dados: DadosCadastro,
): Promise<Usuario> {
  return enviarRequisicaoAuth<Usuario>(
    "/api/auth/register",
    {
      nome: dados.nome.trim(),
      email: dados.email.trim(),
      senha: dados.senha,
    },
    "Não foi possível criar a conta.",
  )
}
