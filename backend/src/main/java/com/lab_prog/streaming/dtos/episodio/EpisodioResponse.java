package com.lab_prog.streaming.dtos.episodio;

import java.util.UUID;

public record EpisodioResponse(
        UUID episodioId,
        UUID serieId,
        Integer temporada,
        Integer numero,
        String titulo,
        Integer duracaoSegundos,
        String urlVideo
) {
}