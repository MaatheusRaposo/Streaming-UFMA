import { NextResponse, type NextRequest } from "next/server"
import {
  COOKIE_ACCESS,
  COOKIE_REFRESH,
  DURACAO_ACCESS_SEGUNDOS,
  DURACAO_REFRESH_SEGUNDOS,
  cookieSessao,
  renovarSessao,
  tokenValido,
} from "@/lib/sessao"

// Páginas abertas a quem não entrou. Todo o resto exige login.
const PAGINAS_PUBLICAS = ["/login", "/cadastro"]

// Roda antes de cada página:
// - sem sessão: manda para /login (e volta para a página pedida depois de entrar)
// - accessToken vencido (ou perto de vencer) mas refreshToken válido: renova sem pedir a senha
// - já logado abrindo /login ou /cadastro: manda para o catálogo
export async function proxy(request: NextRequest) {
  const { pathname, search } = request.nextUrl
  const https = request.nextUrl.protocol === "https:" || request.headers.get("x-forwarded-proto") === "https"
  const accessToken = request.cookies.get(COOKIE_ACCESS)?.value
  const refreshToken = request.cookies.get(COOKIE_REFRESH)?.value

  let logado = tokenValido(accessToken)
  let novosTokens: { accessToken: string; refreshToken: string } | null = null
  let sessaoInvalida = false

  if (!logado && tokenValido(refreshToken, 0)) {
    const renovacao = await renovarSessao(refreshToken!)
    if (renovacao.ok) {
      novosTokens = renovacao
      logado = true
      // A página que vai ser montada agora já enxerga o token novo
      request.cookies.set(COOKIE_ACCESS, renovacao.accessToken)
    } else {
      sessaoInvalida = renovacao.invalido
    }
  } else if (!logado && refreshToken) {
    sessaoInvalida = true // refreshToken vencido
  }

  const publica = PAGINAS_PUBLICAS.some((p) => pathname === p || pathname.startsWith(`${p}/`))
  let resposta: NextResponse

  if (pathname === "/") {
    resposta = NextResponse.redirect(new URL(logado ? "/catalogo" : "/login", request.url))
  } else if (publica) {
    resposta = logado
      ? NextResponse.redirect(new URL("/catalogo", request.url))
      : NextResponse.next()
  } else if (!logado) {
    const login = new URL("/login", request.url)
    login.searchParams.set("destino", `${pathname}${search}`)
    resposta = NextResponse.redirect(login)
  } else {
    resposta = NextResponse.next({ request: { headers: request.headers } })
  }

  if (novosTokens) {
    resposta.cookies.set(cookieSessao(COOKIE_ACCESS, novosTokens.accessToken, DURACAO_ACCESS_SEGUNDOS, https))
    resposta.cookies.set(cookieSessao(COOKIE_REFRESH, novosTokens.refreshToken, DURACAO_REFRESH_SEGUNDOS, https))
  } else if (sessaoInvalida) {
    resposta.cookies.delete(COOKIE_ACCESS)
    resposta.cookies.delete(COOKIE_REFRESH)
  }
  return resposta
}

export const config = {
  // Não passa pelas rotas de API (o login acontece lá), pelos arquivos do Next nem por arquivos com extensão
  matcher: ["/((?!api|_next/static|_next/image|favicon.ico|.*\\.[a-zA-Z0-9]+$).*)"],
}
