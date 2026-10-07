package com.lab_prog.streaming.dtos.midia;

import java.util.List;
import java.util.UUID;

import com.lab_prog.streaming.model.entities.StatusMidia;

public record MidiaResponse(
        UUID midiaId,
        String tipo,
        String titulo,
        String sinopse,
        Integer anoLancamento,
        String urlPoster,
        List<String> generos,
        StatusMidia status
) {
}
