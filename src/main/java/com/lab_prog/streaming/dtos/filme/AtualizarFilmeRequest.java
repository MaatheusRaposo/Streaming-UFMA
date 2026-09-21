package com.lab_prog.streaming.dtos.filme;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AtualizarFilmeRequest(
        @NotBlank(message = "O título é obrigatório")
        @Size(max = 200, message = "O título deve ter no máximo 200 caracteres")
        String titulo,

        @NotBlank(message = "A sinopse é obrigatória")
        String sinopse,

        @NotNull(message = "O ano de lançamento é obrigatório")
        @Min(value = 1888, message = "O ano de lançamento é inválido")
        Integer anoLancamento,

        @NotBlank(message = "A URL do pôster é obrigatória")
        String urlPoster,

        @NotEmpty(message = "Informe pelo menos um gênero")
        List<@NotBlank(message = "O gênero não pode ser vazio") String> generos,

        @NotNull(message = "A duração é obrigatória")
        @Min(value = 1, message = "A duração deve ser maior que zero")
        Integer duracaoSegundos
) {
}
