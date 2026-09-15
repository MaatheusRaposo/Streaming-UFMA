package com.lab_prog.streaming.model.entities;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EpisodioTest {

    @Test
    @DisplayName("Deve instanciar episódio com construtor padrão e atribuir valores via setters")
    void deveInstanciarComConstrutorPadrao() {
        Episodio episodio = new Episodio();
        UUID id = UUID.randomUUID();

        episodio.setEpId(id);
        episodio.setTemporada(1);
        episodio.setNumero(5);
        episodio.setDuracaoSegundos(3200);
        episodio.setTitulo("Piloto");
        episodio.setUrl("https://streaming.ufma.br/series/ep1.mp4");

        assertThat(episodio.getEpId()).isEqualTo(id);
        assertThat(episodio.getTemporada()).isEqualTo(1);
        assertThat(episodio.getNumero()).isEqualTo(5);
        assertThat(episodio.getDuracaoSegundos()).isEqualTo(3200);
        assertThat(episodio.getTitulo()).isEqualTo("Piloto");
        assertThat(episodio.getUrl()).isEqualTo("https://streaming.ufma.br/series/ep1.mp4");
    }

    @Test
    @DisplayName("Deve instanciar episódio com todos os argumentos via construtor")
    void deveInstanciarComTodosOsArgumentos() {
        UUID id = UUID.randomUUID();
        Episodio episodio = new Episodio(id, 2, 8, 2700, "O Confronto", "https://streaming.ufma.br/series/s2e8.mp4");

        assertThat(episodio.getEpId()).isEqualTo(id);
        assertThat(episodio.getTemporada()).isEqualTo(2);
        assertThat(episodio.getNumero()).isEqualTo(8);
        assertThat(episodio.getDuracaoSegundos()).isEqualTo(2700);
        assertThat(episodio.getTitulo()).isEqualTo("O Confronto");
        assertThat(episodio.getUrl()).isEqualTo("https://streaming.ufma.br/series/s2e8.mp4");
    }
}

