import type { Metadata } from "next";
import Link from "next/link";
import { notFound } from "next/navigation";
import MidiaCard from "@/components/MidiaCard";
import { carregarMidia, carregarMidias } from "@/lib/carregar-midias";
import { formatarData, rotuloTipo } from "@/lib/formatar";
import { Midia } from "@/types/midia";

type Props = {
    params: Promise<{ id: string }>
}

export async function generateMetadata({ params }: Props): Promise<Metadata> {
    const { id } = await params
    const midia = await carregarMidia(id)
    return { title: midia ? `${midia.titulo} | UFMA Play` : "Produção não encontrada | UFMA Play" }
}

function buscarRelacionadas(midia: Midia, todas: Midia[]) {
    return todas
        .filter(
            (m) =>
                m.midiaId !== midia.midiaId &&
                (m.tipo === midia.tipo || m.generos.some((g) => midia.generos.includes(g))),
        )
        .slice(0, 6)
}

export default async function DetalhesMidiaPage({ params }: Props) {
    const { id } = await params
    const { midias } = await carregarMidias()
    const midia = midias.find((m) => m.midiaId === id)

    if (!midia) notFound()

    const ano = midia.dataLancamento.slice(0, 4)
    const rotulo = rotuloTipo(midia.tipo)
    const imagemFundo = midia.urlPoster
    const relacionadas = buscarRelacionadas(midia, midias)

    return (
        <main className="min-h-screen bg-[#1b1018] text-[#fff9f3]">
            <section className="relative overflow-hidden bg-[#351023]">
                {imagemFundo && (
                    <img
                        src={imagemFundo}
                        alt=""
                        aria-hidden="true"
                        className="absolute inset-0 h-full w-full object-cover object-[center_43%] opacity-80"
                    />
                )}

                <div
                    aria-hidden="true"
                    className="absolute inset-0"
                    style={{
                        background:
                            "linear-gradient(90deg,#220b18 0%,rgba(34,11,24,.89) 26%,rgba(34,11,24,.24) 73%),linear-gradient(0deg,#1b1018,transparent 58%)",
                    }}
                />

                <div className="relative mx-auto flex min-h-[480px] w-full max-w-[1470px] items-center px-4 sm:min-h-[510px] sm:px-8 lg:px-12">
                    <div className="max-w-[690px] py-10 sm:pb-20 sm:pt-16">
                        <nav aria-label="Trilha de navegação" className="mb-6 flex items-center gap-2 text-xs text-[#ebd7dc]">
                            <Link href="/" className="hover:underline">
                                Início
                            </Link>
                            <span aria-hidden="true">/</span>
                            <Link href="/catalogo" className="hover:underline">
                                Catálogo
                            </Link>
                            <span aria-hidden="true">/</span>
                            <span aria-current="page" className="font-semibold text-[#fff9f3]">
                                {midia.titulo}
                            </span>
                        </nav>

                        <span className="flex items-center gap-2.5 text-[11px] font-extrabold uppercase tracking-[0.22em] text-[#efd5aa]">
                            <span aria-hidden="true" className="h-0.5 w-5 bg-[#efd5aa]" />
                            {rotulo}
                        </span>

                        <h1 className="mt-4 text-[42px] font-extrabold leading-[1.05] tracking-tight sm:text-6xl lg:text-7xl">
                            {midia.titulo}
                        </h1>

                        <div className="mt-4 flex flex-wrap items-center gap-2.5 text-xs text-[#f5e1e4]">
                            <strong className="text-white">{ano}</strong>
                            {midia.generos.length > 0 && (
                                <>
                                    <Separador />
                                    <span>{midia.generos.join(", ")}</span>
                                </>
                            )}
                        </div>

                        <div className="mt-7 flex flex-wrap gap-3">
                            <Link
                                href={`/assistir/${midia.midiaId}`}
                                className="inline-flex min-h-[47px] items-center gap-2.5 rounded-md bg-[#eac38b] px-5 text-[12px] font-extrabold text-[#421a2d] transition hover:-translate-y-0.5 hover:bg-[#f5d6a9] focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#ffcb70] focus-visible:ring-offset-2 focus-visible:ring-offset-[#220b18]"
                            >
                                <IconePlay />
                                {midia.tipo === "Serie" ? "Assistir primeiro episódio" : "Assistir agora"}
                            </Link>
                            <Link
                                href="/catalogo"
                                className="inline-flex min-h-[47px] items-center gap-2.5 rounded-md border border-[#ffedde]/45 bg-[#3f1527]/45 px-5 text-[12px] font-extrabold text-[#fffaf6] backdrop-blur transition hover:-translate-y-0.5 hover:bg-[#69243e]/60 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#ffcb70]"
                            >
                                <IconeVoltar />
                                Voltar ao catálogo
                            </Link>
                        </div>
                    </div>
                </div>
            </section>

            <div className="mx-auto w-full max-w-[1470px] px-4 pb-20 sm:px-8 lg:px-12">
                <div className="grid gap-6 pt-8 lg:grid-cols-[minmax(0,1.8fr)_minmax(260px,1fr)] lg:gap-20 lg:pt-11">
                    <section>
                        <span className="text-[11px] font-extrabold uppercase tracking-[0.16em] text-[#e9bd79]">
                            Sobre a produção
                        </span>
                        <h2 className="mb-4 mt-2 text-2xl font-bold tracking-tight">Sinopse</h2>
                        <p className="text-sm leading-[1.9] text-[#d5bec8]">
                            {midia.sinopse || "Sinopse ainda não cadastrada para esta produção."}
                        </p>
                    </section>

                    <aside
                        aria-label="Ficha técnica"
                        className="grid grid-cols-2 gap-x-4 gap-y-5 border-t border-[#604052] pt-6 lg:block lg:space-y-5 lg:border-l lg:border-t-0 lg:pl-9 lg:pt-0"
                    >
                        <Fato rotulo="Tipo" valor={rotulo} />
                        <Fato rotulo={midia.generos.length > 1 ? "Gêneros" : "Gênero"} valor={midia.generos.join(", ") || "—"} />
                        <Fato rotulo="Lançamento" valor={formatarData(midia.dataLancamento)} />
                    </aside>
                </div>

                {relacionadas.length > 0 && (
                    <section aria-labelledby="titulo-relacionadas" className="mt-14">
                        <span className="text-[11px] font-extrabold uppercase tracking-[0.16em] text-[#e9bd79]">
                            Mais para descobrir
                        </span>
                        <h2 id="titulo-relacionadas" className="mt-2 text-2xl font-bold tracking-tight sm:text-3xl">
                            Você também pode gostar
                        </h2>

                        <div className="mt-6 grid grid-cols-2 gap-x-3 gap-y-6 sm:grid-cols-3 sm:gap-x-4 sm:gap-y-7 md:grid-cols-4 lg:grid-cols-5 xl:grid-cols-6">
                            {relacionadas.map((m) => (
                                <MidiaCard key={m.midiaId} midia={m} />
                            ))}
                        </div>
                    </section>
                )}
            </div>
        </main>
    )
}


function Fato({ rotulo, valor }: { rotulo: string; valor: string }) {
    return (
        <div>
            <strong className="mb-1.5 block text-[10px] font-extrabold uppercase tracking-[0.12em] text-[#b69ca8]">
                {rotulo}
            </strong>
            <span className="text-sm leading-relaxed">{valor}</span>
        </div>
    )
}

function Separador() {
    return (
        <span aria-hidden="true" className="text-[#e9bd79]">
            •
        </span>
    )
}

function IconePlay() {
    return (
        <svg viewBox="0 0 24 24" aria-hidden="true" className="h-4 w-4 fill-current">
            <path d="m8 5 12 7-12 7V5Z" />
        </svg>
    )
}

function IconeVoltar() {
    return (
        <svg
            viewBox="0 0 24 24"
            aria-hidden="true"
            className="h-4 w-4 fill-none stroke-current"
            strokeWidth={1.8}
            strokeLinecap="round"
            strokeLinejoin="round"
        >
            <path d="M19 12H5m7-7-7 7 7 7" />
        </svg>
    )
}