"use client"

import { useState } from "react";

type Props = {
    src: string
    titulo: string
    poster?: string | null
}

export default function VideoPlayer({ src, titulo, poster }: Props) {
    const [falhou, setFalhou] = useState(false)

    return (
        <>
            <div className="mx-auto aspect-video w-full max-w-[1120px] overflow-hidden rounded-md border border-[#76516b] bg-black shadow-[0_18px_50px_rgba(17,4,12,.40)] sm:rounded-xl">
                <video
                    controls
                    playsInline
                    preload="metadata"
                    poster={poster ?? undefined}
                    aria-label={`Player de vídeo: ${titulo}`}
                    onError={() => setFalhou(true)}
                    className="h-full w-full bg-black object-contain"
                >
                    <source src={src} type="video/mp4" onError={() => setFalhou(true)} />
                    Seu navegador não suporta vídeo HTML5.
                </video>
            </div>

            <div
                role="status"
                className="mx-auto mt-4 flex w-full max-w-[1120px] items-start gap-3 rounded-lg border border-[#604052] border-l-[3px] border-l-[#e9bd79] bg-[#321a29] px-5 py-4 text-[13px] leading-relaxed text-[#d5bec8] sm:items-center"
            >
                <IconeInfo />
                {falhou ? (
                    <span>
                        O vídeo não pôde ser carregado. Verifique sua conexão — o restante da página continua disponível.
                    </span>
                ) : (
                    <span>
                        <strong className="text-[#fff9f3]">Vídeo de demonstração:</strong> o player reproduz um trailer
                        aberto de exemplo (Sintel, © Blender Foundation, CC BY 3.0), não a obra selecionada.
                    </span>
                )}
            </div>
        </>
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