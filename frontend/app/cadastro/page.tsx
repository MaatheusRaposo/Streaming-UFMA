import Logo from "@/components/Logo"
import CadastroForm from "@/components/CadastroForm"

export const metadata = {
  title: "Criar conta | UiRA",
}

export default function CadastroPage() {
  return (
    <main className="flex min-h-screen flex-col items-center justify-center gap-8 bg-[#1b1018] px-4 py-10 text-[#fff9f3]">
      <Logo />
      <div className="w-full max-w-sm rounded-2xl border border-[#e9bd79]/20 bg-[#321a29] p-6 shadow-sm sm:p-8">
        <h1 className="mb-6 text-xl font-semibold text-[#fff9f3]">Criar sua conta</h1>
        <CadastroForm />
      </div>
    </main>
  )
}
