import Link from "next/link";

export default function MidiaNaoEncontrada() {
  return (
    <main className="min-h-screen bg-[#1b1018] text-[#fff9f3]">
      <div className="mx-auto flex w-full max-w-[1470px] flex-col items-start gap-3 px-4 py-24 sm:px-8 lg:px-12">
        <span className="flex items-center gap-2.5 text-[11px] font-extrabold uppercase tracking-[0.22em] text-[#e9bd79]">
          <span aria-hidden="true" className="h-0.5 w-5 bg-[#e9bd79]" />
          Erro 404
        </span>
        <h1 className="text-4xl font-extrabold tracking-tight">Produção não encontrada</h1>
        <p className="max-w-md text-[15px] leading-relaxed text-[#d5bec8]">
          O título solicitado não está no catálogo. Ele pode ter sido removido ou o endereço está incorreto.
        </p>
        <Link
          href="/catalogo"
          className="mt-4 inline-flex min-h-[47px] items-center rounded-md bg-[#eac38b] px-5 text-[13px] font-extrabold text-[#421a2d] transition-colors hover:bg-[#f5d6a9]"
        >
          Voltar ao catálogo
        </Link>
      </div>
    </main>
  )
}