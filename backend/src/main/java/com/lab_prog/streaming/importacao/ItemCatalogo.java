package com.lab_prog.streaming.importacao;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Uma linha do crawler/data/catalogo.jsonl gerado pelo crawler do UIRA.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ItemCatalogo(
        String origem,
        String idExterno,
        String tipo,
        String titulo,
        String sinopse,
        String urlPoster,
        List<String> generos,
        String dataLancamento,
        Integer duracaoSegundos,
        String urlVideo,
        String classificacaoIndicativa,
        String uf,
        String tags,
        Map<String, String> fichaTecnica,
        String status,
        String hash
) {
}
