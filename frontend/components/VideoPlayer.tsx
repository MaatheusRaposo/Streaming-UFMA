"use client"

import { useState } from "react";
import type { SituacaoEmbed } from "@/lib/eduplay";

type Props = {
    src: string | null | undefined
    titulo: string
    poster?: string | null
    // Se o EduPlay deixa embutir este vídeo no nosso domínio (consultado no servidor)
    embed?: SituacaoEmbed
}

// Vídeos do UIRA ficam no EduPlay (RNP). O próprio player deles roda dentro de um iframe.
function ehEmbedEduplay(src: string) {
    try {
        const url = new URL(src)
        return url.hostname === "eduplay.rnp.br" && url.pathname.includes("/embed/")
    } catch {
        return false
    }
}

function ehArquivoDeVideo(src: string) {
    return /\.(mp4|webm|ogg)(\?|$)/i.test(src)
}

const moldura =
    "relative mx-auto aspect-video w-full max-w-[1120px] overflow-hidden rounded-md border border-[#76516b] bg-black shadow-[0_18px_50px_rgba(17,4,12,.40)] sm:rounded-xl"

export default function VideoPlayer({ src, titulo, poster, embed }: Props) {
    const [carregou, setCarregou] = useState(false)
    const [falhou, setFalhou] = useState(false)

    if (!src) {
        return (
            <>
                <div className={moldura}>
                    <Capa poster={poster} />
                </div>
                <Aviso>
                    <strong className="text-[#fff9f3]">Vídeo indisponível:</strong> esta produção ainda não tem um
                    vídeo cadastrado.
                </Aviso>
            </>
        )
    }

    if (ehEmbedEduplay(src) && embed?.liberado === false) {
        // O dono do vídeo ainda não liberou o nosso domínio no EduPlay: abre no player oficial da UFMA
        return (
            <>
                <div className={moldura}>
                    <Capa poster={poster} />
                    <div className="absolute inset-0 flex flex-col items-center justify-center gap-4 p-6 text-center">
                        {embed.urlPlayerUfma ? (
                            <a
                                href={embed.urlPlayerUfma}
                                target="_blank"
                                rel="noopener"
                                className="inline-flex min-h-[52px] items-center gap-3 rounded-md bg-[#eac38b] px-6 text-sm font-extrabold text-[#421a2d] shadow-lg transition hover:-translate-y-0.5 hover:bg-[#f5d6a9] focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#ffcb70]"
                            >
                                <svg viewBox="0 0 24 24" aria-hidden="true" className="h-5 w-5 fill-current">
                                    <path d="m8 5 12 7-12 7V5Z" />
                                </svg>
                                Assistir no player da UFMA
                                <span aria-hidden="true">↗</span>
                            </a>
                        ) : (
                            <p className="max-w-md text-sm font-bold text-[#fff9f3]">
                                Este vídeo está temporariamente indisponível fora do UIRA.
                            </p>
                        )}
                    </div>
                </div>
                <Aviso>
                    O EduPlay (RNP) só exibe este vídeo dentro de sites cadastrados pela UFMA. Enquanto o nosso não é
                    cadastrado, ele abre no player oficial da UFMA, em outra aba.
                </Aviso>
            </>
        )
    }

    if (ehEmbedEduplay(src)) {
        return (
            <>
                <div className={moldura}>
                    {!carregou && (
                        <div className="absolute inset-0">
                            <PlayerCarregando poster={poster} />
                        </div>
                    )}
                    <iframe
                        src={src}
                        title={`Player de vídeo: ${titulo}`}
                        allow="autoplay; fullscreen; picture-in-picture; encrypted-media"
                        referrerPolicy="strict-origin-when-cross-origin"
                        onLoad={() => setCarregou(true)}
                        className={`relative h-full w-full border-0 transition-opacity duration-300 ${carregou ? "opacity-100" : "opacity-0"}`}
                    />
                </div>
                <Aviso>
                    Vídeo exibido pelo player do <strong className="text-[#fff9f3]">EduPlay (RNP)</strong>. Use o botão de tela
                    cheia no canto do player.
                    {embed?.liberado == null && embed?.urlPlayerUfma && (
                        <>
                            {" "}Se o vídeo não abrir,{" "}
                            <a href={embed.urlPlayerUfma} target="_blank" rel="noopener" className="font-bold text-[#e9bd79] underline">
                                assista no player da UFMA ↗
                            </a>
                            .
                        </>
                    )}
                </Aviso>
            </>
        )
    }

    if (ehArquivoDeVideo(src)) {
        return (
            <>
                <div className={moldura}>
                    <video
                        controls
                        playsInline
                        preload="metadata"
                        poster={poster ?? undefined}
                        aria-label={`Player de vídeo: ${titulo}`}
                        onError={() => setFalhou(true)}
                        className="h-full w-full bg-black object-contain"
                    >
                        <source src={src} onError={() => setFalhou(true)} />
                        Seu navegador não suporta vídeo HTML5.
                    </video>
                </div>
                {falhou && (
                    <Aviso>
                        O vídeo não pôde ser carregado. Verifique sua conexão — o restante da página continua disponível.
                    </Aviso>
                )}
            </>
        )
    }

    // Outra plataforma: abre na página de origem
    return (
        <>
            <div className={moldura}>
                <Capa poster={poster} />
                <div className="absolute inset-0 flex items-center justify-center">
                    <a
                        href={src}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="rounded-md bg-[#eac38b] px-5 py-3 text-sm font-extrabold text-[#421a2d] hover:bg-[#f5d6a9]"
                    >
                        Assistir na plataforma de origem ↗
                    </a>
                </div>
            </div>
        </>
    )
}

// Enquanto o servidor consulta o EduPlay: o pôster desfocado com um indicador de carregamento
export function PlayerCarregando({ poster }: { poster?: string | null }) {
    return (
        <div className={moldura}>
            <Capa poster={poster} />
            <div className="absolute inset-0 flex items-center justify-center" role="status">
                <span className="h-12 w-12 animate-spin rounded-full border-4 border-[#e9bd79]/30 border-t-[#e9bd79]" />
                <span className="sr-only">Carregando o player…</span>
            </div>
        </div>
    )
}

function Capa({ poster }: { poster?: string | null }) {
    if (!poster) return <div className="absolute inset-0 bg-[#1b1018]" />
    return (
        // eslint-disable-next-line @next/next/no-img-element -- só um fundo desfocado enquanto o player carrega
        <img src={poster} alt="" aria-hidden="true" className="absolute inset-0 h-full w-full scale-110 object-cover opacity-40 blur-md" />
    )
}

function Aviso({ children }: { children: React.ReactNode }) {
    return (
        <div
            role="status"
            className="mx-auto mt-4 flex w-full max-w-[1120px] items-start gap-3 rounded-lg border border-[#604052] border-l-[3px] border-l-[#e9bd79] bg-[#321a29] px-5 py-4 text-[13px] leading-relaxed text-[#d5bec8] sm:items-center"
        >
            <IconeInfo />
            <span>{children}</span>
        </div>
    )
}

function IconeInfo() {
    return (
        <svg
            viewBox="0 0 24 24"
            aria-hidden="true"
            className="h-[19px] w-[19px] flex-none fill-none stroke-[#e9bd79]"
            strokeWidth={1.8}
            strokeLinecap="round"
        >
            <circle cx="12" cy="12" r="10" />
            <path d="M12 11v6M12 7h.01" />
        </svg>
    )
}
