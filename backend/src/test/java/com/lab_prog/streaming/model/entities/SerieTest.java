package com.lab_prog.streaming.model.entities;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SerieTest {

    @Test
    @DisplayName("Deve instanciar Série utilizando o SuperBuilder com atributos próprios e herdados")
    void deveInstanciarSerieComSuperBuilder() {
        UUID id = UUID.randomUUID();
        List<String> generos = List.of("Drama", "Suspense");

        Serie serie = Serie.builder()
                .midiaId(id)
                .titulo("Breaking Bad")
                .sinopse("Um professor de química se torna um produtor de metanfetamina...")
                .anoLancamento(2008)
                .urlPoster("https://streaming.ufma.br/posters/breaking_bad.jpg")
                .generos(generos)
                .totalTemporadas(5)
                .build();

        assertThat(serie).isInstanceOf(Midia.class);
        assertThat(serie.getMidiaId()).isEqualTo(id);
        assertThat(serie.getTitulo()).isEqualTo("Breaking Bad");
        assertThat(serie.getSinopse()).isEqualTo("Um professor de química se torna um produtor de metanfetamina...");
        assertThat(serie.getAnoLancamento()).isEqualTo(2008);
        assertThat(serie.getUrlPoster()).isEqualTo("https://streaming.ufma.br/posters/breaking_bad.jpg");
        assertThat(serie.getGeneros()).containsExactly("Drama", "Suspense");
        assertThat(serie.getTotalTemporadas()).isEqualTo(5);
    }

    @Test
    @DisplayName("Deve instanciar Série com construtor padrão e atribuir valores via setters")
    void deveInstanciarSerieComConstrutorPadraoESetters() {
        Serie serie = new Serie();
        UUID id = UUID.randomUUID();

        serie.setMidiaId(id);
        serie.setTitulo("Dark");
        serie.setSinopse("Desaparecimentos de crianças expõem segredos e conexões temporais.");
        serie.setAnoLancamento(2017);
        serie.setTotalTemporadas(3);

        assertThat(serie.getMidiaId()).isEqualTo(id);
        assertThat(serie.getTitulo()).isEqualTo("Dark");
        assertThat(serie.getSinopse()).isEqualTo("Desaparecimentos de crianças expõem segredos e conexões temporais.");
        assertThat(serie.getAnoLancamento()).isEqualTo(2017);
        assertThat(serie.getTotalTemporadas()).isEqualTo(3);
    }
}

