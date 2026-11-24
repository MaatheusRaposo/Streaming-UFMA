import type { Metadata } from "next";
import Link from "next/link";
import { notFound } from "next/navigation";
import VideoPlayer from "@/components/VideoPlayer";
import { carregarMidia } from "@/lib/carregar-midias";
import { rotuloTipo } from "@/lib/formatar";

const VIDEO_DEMO = "https://media.w3.org/2010/05/sintel/trailer.mp4"

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

  const ano = midia.dataLancamento.slice(0, 4)
  const rotulo = rotuloTipo(midia.tipo)
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
              {rotulo} · {ano}
            </p>
          </div>
        </div>

        <VideoPlayer src={VIDEO_DEMO} titulo={midia.titulo} poster={midia.urlPoster} />

        <div className="mx-auto mt-2 flex w-full max-w-[1120px] flex-col items-start justify-between gap-4 border-b border-[#604052] py-6 sm:flex-row sm:items-center">
          <div>
            <h2 className="text-xl font-bold tracking-tight">{midia.titulo}</h2>
            <p className="mt-1.5 text-[13px] text-[#d5bec8]">
              {[...midia.generos, ano].join(" · ")}
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