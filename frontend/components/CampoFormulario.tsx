import type { InputHTMLAttributes } from "react"

interface CampoFormularioProps extends InputHTMLAttributes<HTMLInputElement> {
  id: string
  rotulo: string
  erro?: string
}

export default function CampoFormulario({
  id,
  rotulo,
  erro,
  ...props
}: CampoFormularioProps) {
  return (
    <div className="flex flex-col gap-1.5">
      <label htmlFor={id} className="text-sm font-medium text-[#fff9f3]">
        {rotulo}
      </label>
      <input
        id={id}
        name={id}
        aria-invalid={erro ? true : undefined}
        aria-describedby={erro ? `${id}-erro` : undefined}
        className={`rounded-lg border bg-[#1b1018] px-3 py-2.5 text-[#fff9f3] outline-none transition-colors placeholder:text-[#b69ca8] focus:ring-2 focus:ring-[#ffcb70]/30 ${
          erro
            ? "border-[#f87171]"
            : "border-[#604052] focus:border-[#e9bd79]"
        }`}
        {...props}
      />
      {erro && (
        <p id={`${id}-erro`} className="text-sm text-[#f87171]">
          {erro}
        </p>
      )}
    </div>
  )
}
