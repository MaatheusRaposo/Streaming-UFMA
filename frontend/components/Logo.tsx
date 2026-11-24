import Image from "next/image"

export default function Logo() {
  return (
    <div className="flex flex-col items-center gap-3">
      <Image
        src="/ufma.svg"
        alt="Logo da UFMA"
        width={88}
        height={88}
        priority
      />
      <span className="text-4xl font-bold tracking-tight text-[#e9bd79]">
        UiRA
      </span>
    </div>
  )
}
