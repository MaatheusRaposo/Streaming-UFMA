export default async function CatalogoPage() {
  const resposta = await fetch(`${process.env.API_URL}/contents`, {
    cache: "no-store",
  });

  const midias = await resposta.json();

  return (
    <main>
      <h1>Catálogo</h1>
      <ul>
        {midias.map((midia: any) => (
          <li key={midia.midiaId}>{midia.titulo}</li>
        ))}
      </ul>
    </main>
  );
}