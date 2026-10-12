package com.lab_prog.streaming.dtos.progresso;

import java.util.Date;
import java.util.UUID;

public record ProgressoVisualizacaoResponse(
        UUID usuarioId,
        UUID conteudoId,
        Integer tempoAssistidoSegundos,
        Date ultimaVisualizacao,
        boolean concluido
) {
}
