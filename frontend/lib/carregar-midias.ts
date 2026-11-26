import { getMidia, getMidias } from "@/lib/api";
import { midiasExemplo } from "@/lib/mock-midias";
import { Midia } from "@/types/midia";

export async function carregarMidias(): Promise<{ midias: Midia[]; usandoExemplo: boolean }> {
  try {
    const midias = await getMidias()
    if (midias.length > 0) {
      return { midias, usandoExemplo: false }
    }
  } catch (erro) {
    console.error(erro)
  }

  return { midias: midiasExemplo, usandoExemplo: true }
}

// Busca só a mídia pedida; se o backend estiver fora, procura nos dados de exemplo
export async function carregarMidia(id: string): Promise<Midia | undefined> {
  try {
    const midia = await getMidia(id)
    if (midia) return midia
  } catch (erro) {
    console.error(erro)
  }
  return midiasExemplo.find((m) => m.midiaId === id)
}
