import { getMidias } from "@/lib/api";
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

export async function carregarMidia(id: string): Promise<Midia | undefined> {
  const { midias } = await carregarMidias()
  return midias.find((m) => m.midiaId === id)
}