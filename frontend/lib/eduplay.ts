// Vídeos do UIRA ficam no EduPlay (RNP). O dono de cada vídeo escolhe em quais domínios ele pode ser
// embutido ("domainsEmbed"); fora deles, o player do EduPlay mostra "Content unavailable for embedding".

const RE_EMBED = /^https:\/\/eduplay\.rnp\.br\/app\/video\/embed\/(\d+)/
const DOMINIO_PLAYER_UFMA = "dados-guarnice.ufma.br"

export type SituacaoEmbed = {
  // null quando não deu para consultar o EduPlay: o player tenta embutir mesmo assim
  liberado: boolean | null
  // Player público da UFMA, para abrir em outra aba quando o vídeo não pode ser embutido aqui
  urlPlayerUfma: string | null
}

export function idVideoEduplay(urlVideo: string | null | undefined): string | null {
  return urlVideo ? (RE_EMBED.exec(urlVideo)?.[1] ?? null) : null
}

// Pergunta ao EduPlay se o vídeo pode ser embutido no nosso site. A resposta fica em cache por 1 hora:
// quando a UFMA cadastrar o nosso domínio, o player passa a abrir aqui dentro sem mudar código.
function urlPlayerUfma(id: string) {
  return `https://${DOMINIO_PLAYER_UFMA}/public/player?videoId=${id}&cinemateca=true`
}

export async function consultarEmbed(urlVideo: string | null | undefined, origemSite: string): Promise<SituacaoEmbed> {
  const id = idVideoEduplay(urlVideo)
  if (!id) return { liberado: null, urlPlayerUfma: null }
  // Se o EduPlay não responder, não dá para saber: tenta embutir e oferece o player da UFMA como plano B
  const desconhecido = { liberado: null, urlPlayerUfma: urlPlayerUfma(id) }

  try {
    const resposta = await fetch(
      `https://eduplay.rnp.br/api/v1/videos/embed/${id}?referrer=${encodeURIComponent(`${origemSite}/`)}`,
      { next: { revalidate: 3600 }, signal: AbortSignal.timeout(3000) },
    )
    if (!resposta.ok) return desconhecido

    const dados = (await resposta.json()) as { embedAllowed?: boolean; domainsEmbed?: string[] }
    const ufmaLiberada = dados.domainsEmbed?.includes(DOMINIO_PLAYER_UFMA) ?? false
    return {
      liberado: typeof dados.embedAllowed === "boolean" ? dados.embedAllowed : null,
      urlPlayerUfma: ufmaLiberada ? urlPlayerUfma(id) : null,
    }
  } catch {
    return desconhecido
  }
}
