import { NextRequest, NextResponse } from "next/server"
import { enviarAuthParaBackend } from "@/lib/auth-server"
import type { RespostaLogin } from "@/types/usuario"

const DURACAO_ACCESS_TOKEN_SEGUNDOS = 15 * 60
const DURACAO_REFRESH_TOKEN_SEGUNDOS = 7 * 24 * 60 * 60

export async function POST(request: NextRequest) {
  let corpo: unknown
  try {
    corpo = await request.json()
  } catch {
    return NextResponse.json(
      { erro: "Requisição inválida." },
      { status: 400 },
    )
  }

  const dados = corpo as Partial<{ email: string; senha: string }>
  const email = typeof dados.email === "string" ? dados.email.trim() : ""
  const senha = typeof dados.senha === "string" ? dados.senha : ""

  if (!email || !senha) {
    return NextResponse.json(
      { erro: "Informe e-mail e senha." },
      { status: 400 },
    )
  }

  const resultado = await enviarAuthParaBackend<RespostaLogin>(
    "/usuario/login",
    { email, senha },
    {
      400: "Dados de login inválidos.",
      401: "E-mail ou senha incorretos.",
      403: "E-mail ou senha incorretos.",
    },
    "Não foi possível entrar. Tente novamente.",
  )

  if (!resultado.ok) {
    const status = resultado.status === 403 ? 401 : resultado.status
    return NextResponse.json({ erro: resultado.erro }, { status })
  }

  const { accessToken, refreshToken, user } = resultado.dados
  if (!accessToken || !refreshToken || !user) {
    return NextResponse.json(
      { erro: "Resposta inválida do servidor de autenticação." },
      { status: 502 },
    )
  }

  const resposta = NextResponse.json({ user }, { status: 200 })
  const ehHttps =
    request.nextUrl.protocol === "https:" ||
    request.headers.get("x-forwarded-proto") === "https"

  const opcoesBaseCookie = {
    httpOnly: true,
    sameSite: "lax" as const,
    path: "/",
    secure: ehHttps,
  }

  resposta.cookies.set("accessToken", accessToken, {
    ...opcoesBaseCookie,
    maxAge: DURACAO_ACCESS_TOKEN_SEGUNDOS,
  })
  resposta.cookies.set("refreshToken", refreshToken, {
    ...opcoesBaseCookie,
    maxAge: DURACAO_REFRESH_TOKEN_SEGUNDOS,
  })

  return resposta
}
