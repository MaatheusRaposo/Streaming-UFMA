import Image from "next/image";

export default function Home() {
  return (
    <div>
      <header className="p-5 justify-between flex">
        <div className="flex h-10 w-10 rounded-xl items-center justify-center bg-red-600">a</div>
        <div className="bg-gray-800 gap-5 p-5 rounded-full flex">
          <div>buscar</div>
          <div>home</div>
          <div>series</div>
          <div>filmes</div>
          <div>jogos</div>
        </div>
      </header>
      <h1 className="text-2xl mx-5 font-bold">Só no nosso streaming</h1>
      <div className="rounded-xl border-white border-2 m-5 px-5 pb-100 text-xl">Lorem ipsum dolor sit amet consectetur, adipisicing elit. Quae voluptatem ducimus iste dicta ullam cumque, dolores totam sunt quasi necessitatibus similique! Odit blanditiis minima molestiae ipsa, deleniti repellat exercitationem repellendus?</div>
      <div className="mx-5">
        <div className="flex gap-5">
          <div>titulo</div>
          <div>ano</div>
          <div>temporadas</div>
        </div>
        <div className="my-5">Lorem, ipsum dolor sit amet consectetur adipisicing elit. Alias mollitia sunt recusandae doloremque sapiente voluptate aut nobis tenetur, asperiores consequuntur omnis eligendi dolor magnam! Quas eos excepturi quasi saepe tempora.</div>
      </div>
    </div>
  );
}
