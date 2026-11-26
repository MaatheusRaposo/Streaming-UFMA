// Sessão do usuário: dois cookies HttpOnly com os tokens JWT do backend.
//   accessToken  dura 15 min e prova que a pessoa está logada
//   refreshToken dura 7 dias e serve para pegar um accessToken novo sem pedir a senha de novo

import { obterBaseApiUrl } from "@/lib/api"

export const COOKIE_ACCESS = "accessToken"
export const COOKIE_REFRESH = "refreshToken"
export const DURACAO_ACCESS_SEGUNDOS = 15 * 60
export const DURACAO_REFRESH_SEGUNDOS = 7 * 24 * 60 * 60

// Renova um pouco antes de vencer, para o token não expirar no meio de uma navegação
const MARGEM_RENOVACAO_SEGUNDOS = 60

type CookieSessao = {
  name: string
  value: string
  httpOnly: true
  sameSite: "lax"
  path: "/"
  secure: boolean
  maxAge: number
}

export function cookieSessao(nome: string, valor: string, maxAge: number, https: boolean): CookieSessao {
  return { name: nome, value: valor, httpOnly: true, sameSite: "lax", path: "/", secure: https, maxAge }
}

// Lê o "exp" do JWT sem verificar a assinatura (o segredo fica só no backend). Serve para decidir
// redirecionamentos no proxy; quem garante a autenticação de verdade é o backend, a cada chamada protegida.
function expiracaoDoToken(token: string): number | null {
  try {
    const parte = token.split(".")[1]
    const json = atob(parte.replace(/-/g, "+").replace(/_/g, "/"))
    const exp = JSON.parse(json).exp
    return typeof exp === "number" ? exp : null
  } catch {
    return null
  }
}

export function tokenValido(token: string | undefined, margemSegundos = MARGEM_RENOVACAO_SEGUNDOS): boolean {
  if (!token) return false
  const exp = expiracaoDoToken(token)
  return exp !== null && exp - margemSegundos > Date.now() / 1000
}

export type ResultadoRenovacao =
  | { ok: true; accessToken: string; refreshToken: string }
  // invalido: o backend recusou o refreshToken (apagar os cookies). Senão, foi falha passageira (rede, 5xx).
  | { ok: false; invalido: boolean }

export async function renovarSessao(refreshToken: string): Promise<ResultadoRenovacao> {
  try {
    const resposta = await fetch(`${obterBaseApiUrl()}/usuario/refresh`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ refreshToken }),
      cache: "no-store",
      signal: AbortSignal.timeout(5000),
    })
    if (!resposta.ok) {
      return { ok: false, invalido: resposta.status === 400 || resposta.status === 401 || resposta.status === 403 }
    }
    const dados = (await resposta.json()) as { accessToken?: string; refreshToken?: string }
    if (!dados.accessToken) return { ok: false, invalido: false }
    return { ok: true, accessToken: dados.accessToken, refreshToken: dados.refreshToken ?? refreshToken }
  } catch {
    return { ok: false, invalido: false }
  }
}
