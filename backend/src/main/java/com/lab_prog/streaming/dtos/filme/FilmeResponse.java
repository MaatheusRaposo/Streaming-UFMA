package com.lab_prog.streaming.dtos.filme;

import java.util.List;
import java.util.UUID;

import com.lab_prog.streaming.model.entities.StatusMidia;

public record FilmeResponse(
        UUID midiaId,
        String titulo,
        String sinopse,
        Integer anoLancamento,
        String urlPoster,
        List<String> generos,
        Integer duracaoSegundos,
        StatusMidia status
) {
}
