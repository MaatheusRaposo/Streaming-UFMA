import { NextResponse } from "next/server"
import { COOKIE_ACCESS, COOKIE_REFRESH } from "@/lib/sessao"

// Sair: apaga os cookies da sessão. O backend não guarda sessão (JWT), então não há o que avisar a ele.
export async function POST() {
  const resposta = NextResponse.json({ ok: true })
  resposta.cookies.delete(COOKIE_ACCESS)
  resposta.cookies.delete(COOKIE_REFRESH)
  return resposta
}
