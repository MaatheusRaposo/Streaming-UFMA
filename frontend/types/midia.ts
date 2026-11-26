export type Midia = {
  midiaId: string
  titulo: string
  tipo: string
  sinopse: string | null
  dataLancamento: string | null
  urlPoster: string | null
  generos: string[]
  status: string
  // Só para filmes e episódios
  duracaoSegundos?: number | null
  urlVideo?: string | null
  classificacaoIndicativa?: string | null
  uf?: string | null
  fichaTecnica?: Record<string, string> | null
}

// O que a grade do catálogo precisa de cada mídia (bem menos dados para mandar ao navegador)
export type MidiaResumo = Pick<
  Midia,
  "midiaId" | "titulo" | "tipo" | "dataLancamento" | "urlPoster" | "generos" | "duracaoSegundos"
>

export function resumir(midia: Midia): MidiaResumo {
  return {
    midiaId: midia.midiaId,
    titulo: midia.titulo,
    tipo: midia.tipo,
    dataLancamento: midia.dataLancamento,
    urlPoster: midia.urlPoster,
    generos: midia.generos ?? [],
    duracaoSegundos: midia.duracaoSegundos ?? null,
  }
}
