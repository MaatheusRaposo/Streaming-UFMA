// Aparece na hora em que a pessoa entra no catálogo, enquanto os filmes chegam do backend
export default function CarregandoCatalogo() {
  return (
    <main className="min-h-screen bg-[#1b1018] text-[#fff9f3]" aria-busy="true">
      <div className="mx-auto w-full max-w-[1470px] px-4 pb-20 pt-10 sm:px-8 lg:px-12">
        <div className="mb-6 h-3 w-32 animate-pulse rounded bg-[#321a29]" />
        <div className="h-3 w-44 animate-pulse rounded bg-[#4a2438]" />
        <div className="mt-4 h-10 w-full max-w-xl animate-pulse rounded bg-[#321a29]" />
        <div className="mt-4 h-4 w-full max-w-2xl animate-pulse rounded bg-[#2a1622]" />

        <div className="mt-8 h-[84px] animate-pulse border-y border-[#604052] bg-[#22121c]" />

        <p role="status" className="sr-only">
          Carregando o catálogo…
        </p>
        <div className="mt-7 grid grid-cols-2 gap-x-3 gap-y-6 sm:grid-cols-3 sm:gap-x-4 sm:gap-y-7 md:grid-cols-4 lg:grid-cols-5 xl:grid-cols-6">
          {Array.from({ length: 18 }, (_, i) => (
            <div key={i}>
              <div className="aspect-[3/4] animate-pulse rounded-lg bg-[#321a29]" />
              <div className="mt-3 h-3.5 w-4/5 animate-pulse rounded bg-[#321a29]" />
              <div className="mt-2 h-3 w-1/2 animate-pulse rounded bg-[#2a1622]" />
            </div>
          ))}
        </div>
      </div>
    </main>
  )
}
