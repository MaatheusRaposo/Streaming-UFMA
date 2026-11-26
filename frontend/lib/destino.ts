// Só aceita caminhos internos: evita usar o ?destino= do login para mandar a pessoa para outro site
export function destinoSeguro(destino: string | null | undefined, padrao = "/catalogo"): string {
  if (!destino || !destino.startsWith("/") || destino.startsWith("//") || destino.startsWith("/\\")) return padrao
  return destino
}
