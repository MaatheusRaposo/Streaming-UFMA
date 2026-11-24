import { NextRequest, NextResponse } from "next/server"
import { enviarAuthParaBackend } from "@/lib/auth-server"
import type { Usuario } from "@/types/usuario"

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

  const dados = corpo as Partial<{ nome: string; email: string; senha: string }>
  const nome = typeof dados.nome === "string" ? dados.nome.trim() : ""
  const email = typeof dados.email === "string" ? dados.email.trim() : ""
  const senha = typeof dados.senha === "string" ? dados.senha : ""

  if (!nome || !email || !senha) {
    return NextResponse.json(
      { erro: "Preencha nome, e-mail e senha." },
      { status: 400 },
    )
  }

  const resultado = await enviarAuthParaBackend<Usuario>(
    "/usuario/register",
    { nome, email, senha },
    {
      400: "Dados inválidos. Verifique os campos informados.",
      409: "Este e-mail já está cadastrado.",
    },
    "Não foi possível criar a conta. Tente novamente.",
  )

  if (!resultado.ok) {
    return NextResponse.json(
      { erro: resultado.erro },
      { status: resultado.status },
    )
  }

  return NextResponse.json(resultado.dados, { status: 201 })
}
