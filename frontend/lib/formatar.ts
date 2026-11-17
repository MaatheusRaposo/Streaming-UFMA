const rotulosTipo: Record<string, string> = {
  Filme: "Filme",
  Serie: "Série",
  Episodio: "Episódio",
}

export function rotuloTipo(tipo: string) {
  return rotulosTipo[tipo] ?? tipo
}

export function formatarData(data: string) {
  const [ano, mes, dia] = data.slice(0, 10).split("-")
  return `${dia}/${mes}/${ano}`
}

export function formatarDuracao(minutos: number) {
  const h = Math.floor(minutos / 60)
  const m = minutos % 60
  if (h === 0) return `${m}min`
  return m === 0 ? `${h}h` : `${h}h ${m}min`
}