"use client"

import { useState } from "react"
import { useRouter } from "next/navigation"

export default function BotaoSair() {
  const router = useRouter()
  const [saindo, setSaindo] = useState(false)

  async function sair() {
    setSaindo(true)
    try {
      await fetch("/api/auth/logout", { method: "POST" })
    } finally {
      router.push("/login")
      router.refresh()
    }
  }

  return (
    <button
      type="button"
      onClick={sair}
      disabled={saindo}
      className="rounded-md border border-[#604052] bg-[#321a29] px-3 py-1.5 text-xs font-bold text-[#fff9f3] transition-colors hover:bg-[#432335] disabled:opacity-60"
    >
      {saindo ? "Saindo…" : "Sair"}
    </button>
  )
}
