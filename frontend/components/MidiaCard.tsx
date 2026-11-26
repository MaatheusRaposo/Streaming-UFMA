import Link from "next/link";
import { anoDe, formatarDuracaoSegundos, rotuloTipo } from "@/lib/formatar";
import { MidiaResumo } from "@/types/midia";

type Props = {
    midia: MidiaResumo
    // Os primeiros cards da tela carregam o pôster na hora; o resto só quando chega perto da tela
    prioridade?: boolean
}

export default function MidiaCard({ midia, prioridade = false }: Props) {
    const ano = anoDe(midia.dataLancamento)
    const genero = midia.generos[0]
    const rotulo = rotuloTipo(midia.tipo)
    const duracao = formatarDuracaoSegundos(midia.duracaoSegundos)
    const detalhes = [ano, genero, duracao].filter(Boolean).join(" · ")

    return (
        <Link
            href={`/catalogo/${midia.midiaId}`}
            aria-label={`Conhecer ${midia.titulo}, ${rotulo}${ano ? ` de ${ano}` : ""}`}
            className="group block rounded-lg outline-none focus-visible:ring-2 focus-visible:ring-[#ffcb70] focus-visible:ring-offset-4 focus-visible:ring-offset-[#1b1018]"
        >
            <div className="relative aspect-[3/4] overflow-hidden rounded-lg bg-[#321a29] shadow-lg ring-1 ring-inset ring-[#e9bd79]/20 transition-transform duration-200 group-hover:-translate-y-1">
                {midia.urlPoster ? (
                    // eslint-disable-next-line @next/next/no-img-element -- pôster já vem otimizado do bucket
                    <img
                        src={midia.urlPoster}
                        alt={`Pôster de ${midia.titulo}`}
                        width={300}
                        height={400}
                        loading={prioridade ? "eager" : "lazy"}
                        fetchPriority={prioridade ? "high" : "auto"}
                        decoding="async"
                        className="h-full w-full object-cover transition-transform duration-300 group-hover:scale-105"
                    />
                ) : (
                    <div className="flex h-full items-center justify-center p-4 text-center text-lg font-bold leading-snug text-[#e9bd79]">
                        {midia.titulo}
                    </div>
                )}

                <span className="absolute left-3 top-3 rounded border border-[#ffe9bc]/25 bg-[#360f24]/90 px-2 py-1 text-[9px] font-extrabold uppercase tracking-wider text-[#ffefdb]">
                    {rotulo}
                </span>
            </div>

            <h3 className="mt-3 line-clamp-2 text-sm font-bold leading-snug">{midia.titulo}</h3>
            {detalhes && <p className="mt-1 line-clamp-1 text-xs text-[#d5bec8]">{detalhes}</p>}
        </Link>
    )
}
