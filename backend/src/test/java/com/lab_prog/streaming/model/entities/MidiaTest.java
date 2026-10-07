package com.lab_prog.streaming.model.entities;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MidiaTest {

    @Test
    @DisplayName("Deve instanciar mídia através do SuperBuilder")
    void deveInstanciarComSuperBuilder() {
        UUID id = UUID.randomUUID();
        List<String> generos = List.of("Ação", "Ficção Científica");

        Midia midia = Midia.builder()
                .midiaId(id)
                .titulo("Inception")
                .sinopse("Um ladrão invade os sonhos dos outros...")
                .anoLancamento(2010)
                .urlPoster("https://streaming.ufma.br/posters/inception.jpg")
                .generos(generos)
                .build();

        assertThat(midia.getMidiaId()).isEqualTo(id);
        assertThat(midia.getTitulo()).isEqualTo("Inception");
        assertThat(midia.getSinopse()).isEqualTo("Um ladrão invade os sonhos dos outros...");
        assertThat(midia.getAnoLancamento()).isEqualTo(2010);
        assertThat(midia.getUrlPoster()).isEqualTo("https://streaming.ufma.br/posters/inception.jpg");
        assertThat(midia.getGeneros()).containsExactly("Ação", "Ficção Científica");
    }

    @Test
    @DisplayName("Deve instanciar com construtor padrão e atribuir valores via setters")
    void deveInstanciarComConstrutorPadraoESetters() {
        Midia midia = new Midia();
        UUID id = UUID.randomUUID();
        List<String> generos = new ArrayList<>();
        generos.add("Drama");

        midia.setMidiaId(id);
        midia.setTitulo("Interestelar");
        midia.setSinopse("Exploradores viajam pelo buraco de minhoca no espaço.");
        midia.setAnoLancamento(2014);
        midia.setUrlPoster("https://streaming.ufma.br/posters/interstellar.jpg");
        midia.setGeneros(generos);

        assertThat(midia.getMidiaId()).isEqualTo(id);
        assertThat(midia.getTitulo()).isEqualTo("Interestelar");
        assertThat(midia.getSinopse()).isEqualTo("Exploradores viajam pelo buraco de minhoca no espaço.");
        assertThat(midia.getAnoLancamento()).isEqualTo(2014);
        assertThat(midia.getUrlPoster()).isEqualTo("https://streaming.ufma.br/posters/interstellar.jpg");
        assertThat(midia.getGeneros()).containsExactly("Drama");
    }
}

