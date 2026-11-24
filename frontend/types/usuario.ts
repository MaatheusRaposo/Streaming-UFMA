export interface CredenciaisLogin {
  email: string
  senha: string
}

export interface DadosCadastro {
  nome: string
  email: string
  senha: string
}

export interface Usuario {
  userId: string
  nome: string
  email: string
}

export interface RespostaLogin {
  accessToken: string
  refreshToken: string
  user: Usuario
}
