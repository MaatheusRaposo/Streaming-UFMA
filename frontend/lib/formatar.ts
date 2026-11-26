const rotulosTipo: Record<string, string> = {
  Filme: "Filme",
  Serie: "Série",
  Episodio: "Episódio",
}

export function rotuloTipo(tipo: string) {
  return rotulosTipo[tipo] ?? tipo
}

export function formatarData(data: string | null | undefined) {
  if (!data) return "—"
  const [ano, mes, dia] = data.slice(0, 10).split("-")
  return `${dia}/${mes}/${ano}`
}

export function anoDe(data: string | null | undefined) {
  return data ? data.slice(0, 4) : ""
}

export function formatarDuracao(minutos: number) {
  const h = Math.floor(minutos / 60)
  const m = minutos % 60
  if (h === 0) return `${m}min`
  return m === 0 ? `${h}h` : `${h}h ${m}min`
}

export function formatarDuracaoSegundos(segundos: number | null | undefined) {
  if (!segundos || segundos <= 0) return ""
  if (segundos < 60) return `${segundos}s`
  return formatarDuracao(Math.round(segundos / 60))
}

// Ordem e rótulos da ficha técnica vinda do UIRA
export const ROTULOS_FICHA: [chave: string, rotulo: string][] = [
  ["direcao", "Direção"],
  ["roteiro", "Roteiro"],
  ["producao", "Produção"],
  ["fotografia", "Fotografia"],
  ["edicao", "Montagem"],
  ["arte", "Direção de arte"],
  ["trilhaSonora", "Trilha sonora"],
  ["desenhoDeSom", "Desenho de som"],
  ["elenco", "Elenco"],
  ["mostra", "Mostra"],
  ["complemento", "Mais créditos"],
]
