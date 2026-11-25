import { Midia } from "@/types/midia";

export async function getMidias(): Promise<Midia[]> {
    const resposta = await fetch(`${process.env.API_URL}/contents`, {
        cache: "no-store",
    })

    if (!resposta.ok) {
        throw new Error(`Erro ao buscar mídias: ${resposta.status}`)
    }


    return resposta.json()
}