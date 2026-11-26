import { Midia } from "@/types/midia";

export function obterBaseApiUrl(): string {
  return process.env.API_URL ?? "http://localhost:8080";
}

export async function getMidias(): Promise<Midia[]> {
    const resposta = await fetch(`${obterBaseApiUrl()}/contents`, {
        cache: "no-store",
    })

    if (!resposta.ok) {
        throw new Error(`Erro ao buscar mídias: ${resposta.status}`)
    }


    return resposta.json()
}
