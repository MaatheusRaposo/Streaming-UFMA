import { Midia } from "@/types/midia";

export function obterBaseApiUrl(): string {
  return process.env.API_URL ?? "http://localhost:8080";
}

// O catálogo só muda quando o importador roda, então a lista fica em cache no servidor do Next por 60s:
// quem chega recebe a cópia guardada na hora e, vencido o prazo, ela é atualizada em segundo plano.
// Assim a página não espera o banco (Supabase, nos EUA) a cada visita.
const SEGUNDOS_CACHE_CATALOGO = 60

export async function getMidias(): Promise<Midia[]> {
    const resposta = await fetch(`${obterBaseApiUrl()}/contents`, {
        next: { revalidate: SEGUNDOS_CACHE_CATALOGO },
    })

    if (!resposta.ok) {
        throw new Error(`Erro ao buscar mídias: ${resposta.status}`)
    }

    return resposta.json()
}

// undefined quando a mídia não existe (o backend responde erro para id inexistente ou inativo)
export async function getMidia(id: string): Promise<Midia | undefined> {
    const resposta = await fetch(`${obterBaseApiUrl()}/contents/${encodeURIComponent(id)}`, {
        next: { revalidate: SEGUNDOS_CACHE_CATALOGO },
    })

    if (resposta.status >= 400 && resposta.status < 500) return undefined
    if (!resposta.ok) {
        throw new Error(`Erro ao buscar mídia ${id}: ${resposta.status}`)
    }

    return resposta.json()
}
