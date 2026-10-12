package com.lab_prog.streaming.dtos.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AtualizarPerfilRequest(
    @NotBlank(message = "O nome é obrigatório")
    String nome,

    @Email(message = "E-mail inválido")
    String email
) {
}
