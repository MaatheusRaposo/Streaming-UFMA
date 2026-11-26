"use client"

import { useDeferredValue, useEffect, useMemo, useRef, useState } from "react";
import MidiaCard from "@/components/MidiaCard";
import { anoDe } from "@/lib/formatar";
import { MidiaResumo } from "@/types/midia";

type Ordem = "recentes" | "antigos" | "titulo"

type Props = {
    midias: MidiaResumo[]
}

// Quantos cards desenhar por vez. O catálogo inteiro já está carregado (a busca e os filtros valem para todos),
// mas desenhar 900 cards de uma vez deixaria a página pesada: o próximo bloco entra pouco antes do fim da rolagem.
const CARDS_POR_BLOCO = 48
// Os primeiros cards aparecem sem rolar: os pôsteres deles carregam na hora
const CARDS_PRIORITARIOS = 12

function compararTexto(a: string | null, b: string | null) {
    return (a ?? "").localeCompare(b ?? "")
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
    // A digitação atualiza o campo na hora; a filtragem da grade vem logo em seguida, sem travar o teclado
    const buscaAdiada = useDeferredValue(busca)

    const generos = useMemo(
        () =>
            [...new Set(midias.flatMap((m) => m.generos))].sort((a, b) =>
                a.localeCompare(b, "pt-BR"),
            ),
        [midias],
    )

    const anos = useMemo(
        () =>
            [...new Set(midias.map((m) => anoDe(m.dataLancamento)).filter(Boolean))].sort((a, b) =>
                b.localeCompare(a),
            ),
        [midias],
    )

    // Títulos normalizados uma vez só, e não a cada tecla
    const titulosNormalizados = useMemo(
        () => new Map(midias.map((m) => [m.midiaId, normalizar(m.titulo)])),
        [midias],
    )

    const filtradas = useMemo(() => {
        const termo = normalizar(buscaAdiada.trim())

        const lista = midias.filter(
            (m) =>
                (!termo || titulosNormalizados.get(m.midiaId)!.includes(termo)) &&
                (!genero || m.generos.includes(genero)) &&
                (!ano || anoDe(m.dataLancamento) === ano),
        )

        return lista.sort((a, b) => {
            if (ordem === "titulo") return a.titulo.localeCompare(b.titulo, "pt-BR")
            const porData =
                ordem === "antigos"
                    ? compararTexto(a.dataLancamento, b.dataLancamento)
                    : compararTexto(b.dataLancamento, a.dataLancamento)
            return porData || a.titulo.localeCompare(b.titulo, "pt-BR")
        })
    }, [midias, titulosNormalizados, buscaAdiada, genero, ano, ordem])

    // Quantos cards estão desenhados. Volta ao primeiro bloco sempre que a busca ou os filtros mudam.
    const chaveFiltros = `${buscaAdiada}|${genero}|${ano}|${ordem}`
    const [blocos, setBlocos] = useState({ chave: chaveFiltros, quantidade: CARDS_POR_BLOCO })
    const quantidade = blocos.chave === chaveFiltros ? blocos.quantidade : CARDS_POR_BLOCO
    const visiveis = filtradas.slice(0, quantidade)
    const temMais = quantidade < filtradas.length

    const sentinela = useRef<HTMLDivElement>(null)
    useEffect(() => {
        const alvo = sentinela.current
        if (!alvo || !temMais) return

        const observador = new IntersectionObserver(
            (entradas) => {
                if (entradas.some((e) => e.isIntersecting)) {
                    setBlocos({ chave: chaveFiltros, quantidade: quantidade + CARDS_POR_BLOCO })
                }
            },
            // Começa a desenhar o próximo bloco um pouco antes de a rolagem chegar ao fim
            { rootMargin: "1200px 0px" },
        )
        observador.observe(alvo)
        return () => observador.disconnect()
    }, [chaveFiltros, quantidade, temMais])

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
                <>
                    <div className="mt-7 grid grid-cols-2 gap-x-3 gap-y-6 sm:grid-cols-3 sm:gap-x-4 sm:gap-y-7 md:grid-cols-4 lg:grid-cols-5 xl:grid-cols-6">
                        {visiveis.map((midia, i) => (
                            <MidiaCard key={midia.midiaId} midia={midia} prioridade={i < CARDS_PRIORITARIOS} />
                        ))}
                    </div>

                    {temMais && (
                        <div ref={sentinela} className="mt-10 flex justify-center">
                            <button
                                type="button"
                                onClick={() => setBlocos({ chave: chaveFiltros, quantidade: quantidade + CARDS_POR_BLOCO })}
                                className="rounded-md border border-[#604052] bg-[#321a29] px-5 py-3 text-sm font-bold text-[#fff9f3] transition-colors hover:bg-[#432335]"
                            >
                                Mostrar mais ({filtradas.length - quantidade} restantes)
                            </button>
                        </div>
                    )}
                </>
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