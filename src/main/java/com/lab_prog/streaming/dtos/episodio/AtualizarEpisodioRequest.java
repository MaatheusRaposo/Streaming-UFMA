package com.lab_prog.streaming.dtos.episodio;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AtualizarEpisodioRequest(
        @NotBlank(message = "O título é obrigatório")
        @Size(max = 200, message = "O título deve ter no máximo 200 caracteres")
        String titulo,

        @NotNull(message = "A duração é obrigatória")
        @Min(value = 1, message = "A duração deve ser maior que zero")
        Integer duracaoSegundos,

        @NotBlank(message = "A URL do vídeo é obrigatória")
        String urlVideo
) {
}