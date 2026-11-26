"use client"

import { useState, type FormEvent } from "react"
import { useRouter } from "next/navigation"
import Link from "next/link"
import CampoFormulario from "@/components/CampoFormulario"
import { fazerLogin } from "@/lib/auth"

const REGEX_EMAIL = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

interface Erros {
  email?: string
  senha?: string
  geral?: string
}

interface LoginFormProps {
  contaCriada?: boolean
}

export default function LoginForm({ contaCriada = false }: LoginFormProps) {
  const router = useRouter()
  const [email, setEmail] = useState("")
  const [senha, setSenha] = useState("")
  const [erros, setErros] = useState<Erros>({})
  const [enviando, setEnviando] = useState(false)

  function validar(): Erros {
    const novos: Erros = {}
    if (!REGEX_EMAIL.test(email.trim())) novos.email = "Digite um e-mail válido."
    if (!senha) novos.senha = "Digite sua senha."
    return novos
  }

  async function aoEnviar(evento: FormEvent<HTMLFormElement>) {
    evento.preventDefault()

    const novosErros = validar()
    setErros(novosErros)
    if (Object.keys(novosErros).length > 0) return

    setEnviando(true)
    try {
      await fazerLogin({ email: email.trim(), senha })
      router.push("/catalogo")
      router.refresh()
    } catch (e) {
      setErros({
        geral: e instanceof Error ? e.message : "Não foi possível entrar. Tente novamente.",
      })
    } finally {
      setEnviando(false)
    }
  }

  return (
    <form onSubmit={aoEnviar} noValidate className="flex flex-col gap-4">
      {contaCriada && !erros.geral && (
        <p
          role="status"
          className="rounded-lg border border-[#e9bd79]/35 bg-[#e9bd79]/10 px-3 py-2 text-sm text-[#ffefdb]"
        >
          Conta criada com sucesso! Entre com seu e-mail e senha.
        </p>
      )}

      <CampoFormulario
        id="email"
        rotulo="E-mail"
        type="email"
        autoComplete="email"
        placeholder="voce@exemplo.com"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
        erro={erros.email}
      />

      <CampoFormulario
        id="senha"
        rotulo="Senha"
        type="password"
        autoComplete="current-password"
        value={senha}
        onChange={(e) => setSenha(e.target.value)}
        erro={erros.senha}
      />

      {erros.geral && (
        <p role="alert" className="text-sm text-[#f87171]">
          {erros.geral}
        </p>
      )}

      <button
        type="submit"
        disabled={enviando}
        className="mt-2 rounded-lg bg-[#eac38b] px-4 py-2.5 font-semibold text-[#421a2d] transition-colors hover:bg-[#f5d6a9] focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-[#ffcb70] disabled:cursor-not-allowed disabled:opacity-60"
      >
        {enviando ? "Entrando..." : "Entrar"}
      </button>

      <p className="text-center text-sm text-[#d5bec8]">
        Ainda não tem conta?{" "}
        <Link
          href="/cadastro"
          className="font-semibold text-[#e9bd79] hover:underline"
        >
          Criar conta
        </Link>
      </p>
    </form>
  )
}
