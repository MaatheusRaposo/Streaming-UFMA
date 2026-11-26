import type { Metadata } from "next";
import Link from "next/link";
import { Suspense } from "react";
import { headers } from "next/headers";
import { notFound } from "next/navigation";
import VideoPlayer, { PlayerCarregando } from "@/components/VideoPlayer";
import { carregarMidia } from "@/lib/carregar-midias";
import { consultarEmbed } from "@/lib/eduplay";
import { anoDe, formatarDuracaoSegundos, rotuloTipo } from "@/lib/formatar";
import { Midia } from "@/types/midia";

type Props = {
  params: Promise<{ id: string }>
}

export async function generateMetadata({ params }: Props): Promise<Metadata> {
  const { id } = await params
  const midia = await carregarMidia(id)
  return { title: midia ? `Assistir ${midia.titulo} | UFMA Play` : "Produção não encontrada | UFMA Play" }
}

export default async function AssistirPage({ params }: Props) {
  const { id } = await params
  const midia = await carregarMidia(id)

  if (!midia) notFound()

  const ano = anoDe(midia.dataLancamento)
  const rotulo = rotuloTipo(midia.tipo)
  const duracao = formatarDuracaoSegundos(midia.duracaoSegundos)
  const linkDetalhes = `/catalogo/${midia.midiaId}`

  return (
    <main className="min-h-screen bg-[#1b1018] text-[#fff9f3]">
      <section className="mx-auto w-full max-w-[1470px] px-4 pb-20 pt-6 sm:px-8 sm:pt-8 lg:px-12">
        <div className="mb-6 flex items-start justify-between gap-4 sm:items-center">
          <div>
            <Link
              href={linkDetalhes}
              className="inline-flex items-center gap-1.5 text-xs font-bold text-[#e9bd79] hover:underline"
            >
              <IconeVoltar />
              Voltar para detalhes
            </Link>
            <h1 className="mt-1.5 text-xl font-extrabold tracking-tight sm:text-2xl">{midia.titulo}</h1>
            <p className="mt-1.5 text-xs text-[#d5bec8]">
              {[rotulo, ano, duracao].filter(Boolean).join(" · ")}
            </p>
          </div>
        </div>

        {/* A página aparece na hora; só o quadro do player espera a resposta do EduPlay */}
        <Suspense fallback={<PlayerCarregando poster={midia.urlPoster} />}>
          <PlayerDoFilme midia={midia} />
        </Suspense>

        <div className="mx-auto mt-2 flex w-full max-w-[1120px] flex-col items-start justify-between gap-4 border-b border-[#604052] py-6 sm:flex-row sm:items-center">
          <div>
            <h2 className="text-xl font-bold tracking-tight">{midia.titulo}</h2>
            <p className="mt-1.5 text-[13px] text-[#d5bec8]">
              {[...midia.generos, ano].filter(Boolean).join(" · ")}
            </p>
          </div>

          <Link
            href={linkDetalhes}
            className="inline-flex min-h-[39px] items-center rounded-md border border-[#604052] bg-[#321a29] px-4 text-[13px] font-bold text-[#fff9f3] transition-colors hover:bg-[#432335]"
          >
            Ver sinopse e ficha técnica
          </Link>
        </div>
      </section>
    </main>
  )
}

// O EduPlay só deixa embutir o vídeo nos domínios que o dono cadastrou: pergunta se o nosso está entre eles
async function PlayerDoFilme({ midia }: { midia: Midia }) {
  const cabecalhos = await headers()
  const host = cabecalhos.get("x-forwarded-host") ?? cabecalhos.get("host") ?? "localhost:3000"
  const protocolo = cabecalhos.get("x-forwarded-proto") ?? (host.startsWith("localhost") ? "http" : "https")
  const embed = await consultarEmbed(midia.urlVideo, `${protocolo}://${host}`)

  return <VideoPlayer src={midia.urlVideo} titulo={midia.titulo} poster={midia.urlPoster} embed={embed} />
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