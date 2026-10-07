package com.lab_prog.streaming.model.entities;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FilmeTest {

    @Test
    @DisplayName("Deve instanciar Filme utilizando o SuperBuilder com atributos próprios e herdados")
    void deveInstanciarFilmeComSuperBuilder() {
        UUID id = UUID.randomUUID();
        List<String> generos = List.of("Ação", "Aventura");

        Filme filme = Filme.builder()
                .midiaId(id)
                .titulo("Matrix")
                .sinopse("Um hacker descobre a verdadeira natureza da sua realidade...")
                .anoLancamento(1999)
                .urlPoster("https://streaming.ufma.br/posters/matrix.jpg")
                .generos(generos)
                .duracaoSegundos(8160)
                .build();

        assertThat(filme).isInstanceOf(Midia.class);
        assertThat(filme.getMidiaId()).isEqualTo(id);
        assertThat(filme.getTitulo()).isEqualTo("Matrix");
        assertThat(filme.getSinopse()).isEqualTo("Um hacker descobre a verdadeira natureza da sua realidade...");
        assertThat(filme.getAnoLancamento()).isEqualTo(1999);
        assertThat(filme.getUrlPoster()).isEqualTo("https://streaming.ufma.br/posters/matrix.jpg");
        assertThat(filme.getGeneros()).containsExactly("Ação", "Aventura");
        assertThat(filme.getDuracaoSegundos()).isEqualTo(8160);
    }

    @Test
    @DisplayName("Deve instanciar Filme com construtor padrão e atribuir valores via setters")
    void deveInstanciarFilmeComConstrutorPadraoESetters() {
        Filme filme = new Filme();
        UUID id = UUID.randomUUID();

        filme.setMidiaId(id);
        filme.setTitulo("Gladiador");
        filme.setSinopse("Um general romano se torna gladiador...");
        filme.setAnoLancamento(2000);
        filme.setDuracaoSegundos(9300);

        assertThat(filme.getMidiaId()).isEqualTo(id);
        assertThat(filme.getTitulo()).isEqualTo("Gladiador");
        assertThat(filme.getSinopse()).isEqualTo("Um general romano se torna gladiador...");
        assertThat(filme.getAnoLancamento()).isEqualTo(2000);
        assertThat(filme.getDuracaoSegundos()).isEqualTo(9300);
    }
}

