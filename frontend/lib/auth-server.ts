import { obterBaseApiUrl } from "@/lib/api"

interface ResultadoBackendSucesso<T> {
  ok: true
  status: number
  dados: T
}

interface ResultadoBackendErro {
  ok: false
  status: number
  erro: string
}

export type ResultadoBackend<T> = ResultadoBackendSucesso<T> | ResultadoBackendErro

const MENSAGENS_GENERICAS_SPRING = new Set([
  "No message available",
  "Unauthorized",
  "Conflict",
  "Bad Request",
  "Forbidden",
  "Internal Server Error",
])

function extrairMensagem(corpo: unknown): string | undefined {
  if (!corpo || typeof corpo !== "object") return undefined

  const registro = corpo as Record<string, unknown>
  for (const chave of ["erro", "message", "detail"]) {
    const valor = registro[chave]
    if (
      typeof valor === "string" &&
      valor.trim().length > 0 &&
      !MENSAGENS_GENERICAS_SPRING.has(valor.trim())
    ) {
      return valor.trim()
    }
  }

  return undefined
}

async function lerJsonSeguro(resposta: Response): Promise<unknown> {
  const texto = await resposta.text()
  if (!texto) return null

  try {
    return JSON.parse(texto)
  } catch {
    return null
  }
}

export async function enviarAuthParaBackend<T>(
  caminho: string,
  payload: unknown,
  mensagensPorStatus: Record<number, string>,
  mensagemPadrao: string,
): Promise<ResultadoBackend<T>> {
  let resposta: Response

  try {
    resposta = await fetch(`${obterBaseApiUrl()}${caminho}`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload),
      cache: "no-store",
    })
  } catch {
    return {
      ok: false,
      status: 503,
      erro: "Não foi possível conectar ao servidor. Tente novamente.",
    }
  }

  const corpo = await lerJsonSeguro(resposta)

  if (!resposta.ok) {
    const mensagem =
      extrairMensagem(corpo) ??
      mensagensPorStatus[resposta.status] ??
      mensagemPadrao

    return {
      ok: false,
      status: resposta.status,
      erro: mensagem,
    }
  }

  if (corpo === null) {
    return {
      ok: false,
      status: 502,
      erro: "Resposta inválida do servidor de autenticação.",
    }
  }

  return {
    ok: true,
    status: resposta.status,
    dados: corpo as T,
  }
}
