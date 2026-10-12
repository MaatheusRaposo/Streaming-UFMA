package com.lab_prog.streaming.dtos.progresso;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AtualizarProgressoRequest(
        @NotNull(message = "O tempo assistido é obrigatório")
        @Min(value = 0, message = "O tempo assistido não pode ser negativo")
        Integer tempoAssistidoSegundos
) {
}
