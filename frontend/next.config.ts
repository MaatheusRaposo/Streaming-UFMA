import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  // A página inicial é o catálogo. Temporário (307) para o navegador não guardar o redirecionamento.
  async redirects() {
    return [{ source: "/", destination: "/catalogo", permanent: false }];
  },
};

export default nextConfig;
