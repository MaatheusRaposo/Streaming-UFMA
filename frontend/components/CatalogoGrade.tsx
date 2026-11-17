"use client"

import { useMemo, useState } from "react";
import MidiaCard from "@/components/MidiaCard";
import { Midia } from "@/types/midia";

type Ordem = "recentes" | "antigos" | "titulo"

type Props = {
    midias: Midia[]
}

function normalizar(texto: string) {
    return texto.normalize("NFD").replace(/[\u0300-\u036f]/g, "").toLowerCase()
}

const estiloCampo = "min-h-[42px] min-w-[150px] rounded-md border border-[#604052] bg-[#321a29] px-3 text-sm text-[#fff9f3] outline-none focus-visible:ring-2 focus-visible:ring-[#ffcb70]"
const estiloRotulo = "text-[11px] font-extrabold text-[#d5bec8]"

export default function CatalogoGrade({ midias }: Props) {
    const [busca, setBusca] = useState("")
    const [genero, setGenero] = useState("")
    const [ano, setAno] = useState("")
    const [ordem, setOrdem] = useState<Ordem>("recentes")

    const generos = useMemo(
        () =>
            [...new Set(midias.flatMap((m) => m.generos))].sort((a, b) =>
                a.localeCompare(b, "pt-BR"),
            ),
        [midias],
    )

    const anos = useMemo(
        () =>
            [...new Set(midias.map((m) => m.dataLancamento.slice(0, 4)))].sort((a, b) =>
                b.localeCompare(a),
            ),
        [midias],
    )

    const filtradas = useMemo(() => {
        const termo = normalizar(busca.trim())

        const lista = midias.filter(
            (m) =>
                (!termo || normalizar(m.titulo).includes(termo)) &&
                (!genero || m.generos.includes(genero)) &&
                (!ano || m.dataLancamento.startsWith(ano)),
        )

        return lista.sort((a, b) => {
            if (ordem === "titulo") return a.titulo.localeCompare(b.titulo, "pt-BR")
            if (ordem === "antigos") return a.dataLancamento.localeCompare(b.dataLancamento)
            return b.dataLancamento.localeCompare(a.dataLancamento)
        })
    }, [midias, busca, genero, ano, ordem])

    function limparFiltros() {
        setBusca("")
        setGenero("")
        setAno("")
        setOrdem("recentes")
    }

    return (
        <>
            <section
                aria-label="Filtrar catálogo"
                className="mt-8 flex flex-col gap-4 border-y border-[#604052] py-5 sm:flex-row sm:items-end sm:justify-between"
            >
                <div className="flex flex-wrap gap-3">
                    <div className="flex flex-1 flex-col gap-1.5 sm:flex-none">
                        <label htmlFor="filtro-busca" className={estiloRotulo}>
                            Buscar por título
                        </label>
                        <input
                            id="filtro-busca"
                            type="search"
                            autoComplete="off"
                            placeholder="Digite um título"
                            value={busca}
                            onChange={(e) => setBusca(e.target.value)}
                            className={`${estiloCampo} placeholder:text-[#b69ca8]`}
                        />
                    </div>

                    <div className="flex flex-1 flex-col gap-1.5 sm:flex-none">
                        <label htmlFor="filtro-genero" className={estiloRotulo}>
                            Gênero
                        </label>
                        <select
                            id="filtro-genero"
                            value={genero}
                            onChange={(e) => setGenero(e.target.value)}
                            className={estiloCampo}
                        >
                            <option value="">Todos os gêneros</option>
                            {generos.map((g) => (
                                <option key={g} value={g}>
                                    {g}
                                </option>
                            ))}
                        </select>
                    </div>

                    <div className="flex flex-1 flex-col gap-1.5 sm:flex-none">
                        <label htmlFor="filtro-ano" className={estiloRotulo}>
                            Ano
                        </label>
                        <select
                            id="filtro-ano"
                            value={ano}
                            onChange={(e) => setAno(e.target.value)}
                            className={estiloCampo}
                        >
                            <option value="">Todos os anos</option>
                            {anos.map((a) => (
                                <option key={a} value={a}>
                                    {a}
                                </option>
                            ))}
                        </select>
                    </div>

                    <div className="flex flex-1 flex-col gap-1.5 sm:flex-none">
                        <label htmlFor="filtro-ordem" className={estiloRotulo}>
                            Ordenar por
                        </label>
                        <select
                            id="filtro-ordem"
                            value={ordem}
                            onChange={(e) => setOrdem(e.target.value as Ordem)}
                            className={estiloCampo}
                        >
                            <option value="recentes">Mais recentes</option>
                            <option value="titulo">Título (A–Z)</option>
                            <option value="antigos">Mais antigos</option>
                        </select>
                    </div>
                </div>

                <span role="status" aria-live="polite" className="whitespace-nowrap text-xs font-bold text-[#d5bec8]">
                    {filtradas.length} {filtradas.length === 1 ? "produção encontrada" : "produções encontradas"}
                </span>
            </section>

            {filtradas.length > 0 ? (
                <div className="mt-7 grid grid-cols-2 gap-x-3 gap-y-6 sm:grid-cols-3 sm:gap-x-4 sm:gap-y-7 md:grid-cols-4 lg:grid-cols-5 xl:grid-cols-6">
                    {filtradas.map((midia) => (
                        <MidiaCard key={midia.midiaId} midia={midia} />
                    ))}
                </div>
            ) : (
                <div className="mt-7 flex flex-col items-center gap-2 rounded-xl border border-dashed border-[#604052] px-5 py-20 text-center text-[#d5bec8]">
                    <h2 className="text-2xl font-bold text-[#fff9f3]">Nenhum resultado encontrado</h2>
                    <p className="max-w-sm text-sm leading-relaxed">
                        Tente outro termo ou altere os filtros para descobrir mais produções.
                    </p>
                    <button
                        type="button"
                        onClick={limparFiltros}
                        className="mt-3 rounded-md border border-[#604052] bg-[#321a29] px-5 py-3 text-sm font-bold text-[#fff9f3] transition-colors hover:bg-[#432335]"
                    >
                        Limpar filtros
                    </button>
                </div>
            )}
        </>
    )
}