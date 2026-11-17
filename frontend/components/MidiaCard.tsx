import Link from "next/link";
import { rotuloTipo } from "@/lib/formatar";
import { Midia } from "@/types/midia";

type Props = {
    midia: Midia
}

export default function MidiaCard({ midia }: Props) {
    const ano = midia.dataLancamento.slice(0, 4)
    const genero = midia.generos[0]
    const rotulo = rotuloTipo(midia.tipo)

    return (
        <Link
            href={`/catalogo/${midia.midiaId}`}
            aria-label={`Conhecer ${midia.titulo}, ${rotulo} de ${ano}`}
            className="group block rounded-lg outline-none focus-visible:ring-2 focus-visible:ring-[#ffcb70] focus-visible:ring-offset-4 focus-visible:ring-offset-[#1b1018]"
        >
            <div className="relative aspect-[3/4] overflow-hidden rounded-lg bg-[#321a29] shadow-lg ring-1 ring-inset ring-[#e9bd79]/20 transition-transform duration-200 group-hover:-translate-y-1">
                {midia.urlPoster ? (
                    <img
                        src={midia.urlPoster}
                        alt={`Pôster de ${midia.titulo}`}
                        loading="lazy"
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

            <h3 className="mt-3 text-sm font-bold leading-snug">{midia.titulo}</h3>
            <p className="mt-1 text-xs text-[#d5bec8]">
                {ano}
                {genero && ` · ${genero}`}
            </p>
        </Link>
    )
}