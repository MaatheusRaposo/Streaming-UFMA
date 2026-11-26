"use client"

import { useState, type FormEvent } from "react"
import { useRouter } from "next/navigation"
import Link from "next/link"
import CampoFormulario from "@/components/CampoFormulario"
import { cadastrarUsuario } from "@/lib/auth"

const REGEX_EMAIL = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
const TAMANHO_MINIMO_SENHA = 8

interface Erros {
  nome?: string
  email?: string
  senha?: string
  confirmarSenha?: string
  geral?: string
}

export default function CadastroForm() {
  const router = useRouter()
  const [nome, setNome] = useState("")
  const [email, setEmail] = useState("")
  const [senha, setSenha] = useState("")
  const [confirmarSenha, setConfirmarSenha] = useState("")
  const [erros, setErros] = useState<Erros>({})
  const [enviando, setEnviando] = useState(false)

  function validar(): Erros {
    const novos: Erros = {}
    const nomeLimpo = nome.trim()
    const emailLimpo = email.trim()

    if (nomeLimpo.length < 2) novos.nome = "Digite seu nome."
    else if (nomeLimpo.length > 100)
      novos.nome = "O nome deve ter no máximo 100 caracteres."

    if (!REGEX_EMAIL.test(emailLimpo)) novos.email = "Digite um e-mail válido."
    else if (emailLimpo.length > 100)
      novos.email = "O e-mail deve ter no máximo 100 caracteres."

    if (senha.length < TAMANHO_MINIMO_SENHA)
      novos.senha = `A senha precisa ter pelo menos ${TAMANHO_MINIMO_SENHA} caracteres.`
    if (confirmarSenha !== senha) novos.confirmarSenha = "As senhas não são iguais."
    return novos
  }

  async function aoEnviar(evento: FormEvent<HTMLFormElement>) {
    evento.preventDefault()

    const novosErros = validar()
    setErros(novosErros)
    if (Object.keys(novosErros).length > 0) return

    setEnviando(true)
    try {
      // A confirmação de senha só serve para validar a digitação: não é enviada.
      await cadastrarUsuario({ nome: nome.trim(), email: email.trim(), senha })
      router.push("/login?cadastro=sucesso")
    } catch (e) {
      setErros({
        geral: e instanceof Error ? e.message : "Não foi possível criar a conta. Tente novamente.",
      })
    } finally {
      setEnviando(false)
    }
  }

  return (
    <form onSubmit={aoEnviar} noValidate className="flex flex-col gap-4">
      <CampoFormulario
        id="nome"
        rotulo="Nome"
        type="text"
        autoComplete="name"
        value={nome}
        onChange={(e) => setNome(e.target.value)}
        erro={erros.nome}
      />

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
        autoComplete="new-password"
        value={senha}
        onChange={(e) => setSenha(e.target.value)}
        erro={erros.senha}
      />

      <CampoFormulario
        id="confirmarSenha"
        rotulo="Confirmar senha"
        type="password"
        autoComplete="new-password"
        value={confirmarSenha}
        onChange={(e) => setConfirmarSenha(e.target.value)}
        erro={erros.confirmarSenha}
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
        {enviando ? "Criando conta..." : "Criar conta"}
      </button>

      <p className="text-center text-sm text-[#d5bec8]">
        Já tem conta?{" "}
        <Link
          href="/login"
          className="font-semibold text-[#e9bd79] hover:underline"
        >
          Entrar
        </Link>
      </p>
    </form>
  )
}
