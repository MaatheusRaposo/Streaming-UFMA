import type { Metadata } from "next";
import Link from "next/link";
import { connection } from "next/server";
import CatalogoGrade from "@/components/CatalogoGrade";
import { carregarMidias } from "@/lib/carregar-midias";
import { resumir } from "@/types/midia";

export const metadata: Metadata = {
  title: "Catálogo | UFMA Play",
}

export default async function CatalogoPage() {
  // Sempre montada na hora do pedido: sem isso, o build gravaria a página com os dados de exemplo
  // (o backend não está de pé durante o build). Os dados em si vêm do cache de 60s do getMidias.
  await connection()
  const { midias } = await carregarMidias()
  // A grade só precisa de título, pôster, ano e gênero: sinopse e ficha técnica ficam no servidor
  const resumos = midias.map(resumir)

  return (
    <main className="min-h-screen bg-[#1b1018] text-[#fff9f3]">
      <div className="mx-auto w-full max-w-[1470px] px-4 pb-20 pt-10 sm:px-8 lg:px-12">
        <nav aria-label="Trilha de navegação" className="mb-6 flex items-center gap-2 text-xs text-[#d5bec8]">
          <Link href="/" className="hover:underline">
            Início
          </Link>
          <span aria-hidden="true">/</span>
          <span className="font-semibold text-[#fff9f3]">Catálogo</span>
        </nav>

        <span className="flex items-center gap-2.5 text-[11px] font-extrabold uppercase tracking-[0.22em] text-[#e9bd79]">
          <span aria-hidden="true" className="h-0.5 w-5 bg-[#e9bd79]" />
          Catálogo audiovisual
        </span>

        <h1 className="mt-3 text-4xl font-extrabold tracking-tight sm:text-5xl">
          Encontre sua próxima história.
        </h1>
        <p className="mt-3 max-w-2xl text-[15px] leading-relaxed text-[#d5bec8]">
          Filmes, séries, documentários e curtas com diferentes olhares sobre cultura, cidade e pertencimento.
        </p>

        <CatalogoGrade midias={resumos} />
      </div>
    </main>
  )
}
