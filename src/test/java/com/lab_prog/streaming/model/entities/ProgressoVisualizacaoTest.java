package com.lab_prog.streaming.model.entities;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ProgressoVisualizacaoTest {

    @Nested
    @DisplayName("Testes de Construtores e Métodos de Acesso (Getters/Setters)")
    class ConstrutoresEMetodosAcesso {

        @Test
        @DisplayName("Deve instanciar com construtor padrão e atribuir campos via setters")
        void deveInstanciarComConstrutorPadraoESetters() {
            ProgressoVisualizacao progresso = new ProgressoVisualizacao();
            Usuario usuario = new Usuario(UUID.randomUUID(), "Alice", "alice@example.com","hash-de-teste");
            Episodio episodio = Episodio.builder().midiaId(UUID.randomUUID()).temporada(1).numero(1).duracaoSegundos(3600).titulo("Ep 1").url("https://ep1.mp4").build();
            ProgressoVisualizacaoId id = new ProgressoVisualizacaoId(usuario.getUserId(), episodio.getMidiaId());
            Date agora = new Date();

            progresso.setPvId(id);
            progresso.setUser(usuario);
            progresso.setConteudo(episodio);
            progresso.setTempoAssistidoSegundos(1800);
            progresso.setUltimaVisualizacao(agora);
            progresso.setConcluido(false);

            assertThat(progresso.getPvId()).isEqualTo(id);
            assertThat(progresso.getUser()).isEqualTo(usuario);
            assertThat(progresso.getConteudo()).isEqualTo(episodio);
            assertThat(progresso.getTempoAssistidoSegundos()).isEqualTo(1800);
            assertThat(progresso.getUltimaVisualizacao()).isEqualTo(agora);
            assertThat(progresso.isConcluido()).isFalse();
        }

        @Test
        @DisplayName("Deve instanciar com construtor com todos os argumentos")
        void deveInstanciarComTodosOsArgumentos() {
            Usuario usuario = new Usuario(UUID.randomUUID(), "Bob", "bob@example.com","hash-de-teste");
            Episodio episodio = Episodio.builder().midiaId(UUID.randomUUID()).temporada(1).numero(2).duracaoSegundos(2400).titulo("Ep 2").url("https://ep2.mp4").build();
            ProgressoVisualizacaoId id = new ProgressoVisualizacaoId(usuario.getUserId(), episodio.getMidiaId());
            Date agora = new Date();

            ProgressoVisualizacao progresso = new ProgressoVisualizacao(
                    id, usuario, episodio, 1200, agora, false
            );

            assertThat(progresso.getPvId()).isEqualTo(id);
            assertThat(progresso.getUser()).isEqualTo(usuario);
            assertThat(progresso.getConteudo()).isEqualTo(episodio);
            assertThat(progresso.getTempoAssistidoSegundos()).isEqualTo(1200);
            assertThat(progresso.getUltimaVisualizacao()).isEqualTo(agora);
            assertThat(progresso.isConcluido()).isFalse();
        }
    }

    @Nested
    @DisplayName("Testes de Métodos de Negócio")
    class MetodosDeNegocio {

        @Test
        @DisplayName("atualizarProgresso() deve alterar o tempoAssistidoSegundos para o valor fornecido")
        void deveAtualizarProgresso() {
            ProgressoVisualizacao progresso = new ProgressoVisualizacao();
            progresso.setTempoAssistidoSegundos(100);

            progresso.atualizarProgresso(350);

            assertThat(progresso.getTempoAssistidoSegundos()).isEqualTo(350);
        }

        @Test
        @DisplayName("marcarConcluido() deve alterar o status concluido para true")
        void deveMarcarComoConcluido() {
            ProgressoVisualizacao progresso = new ProgressoVisualizacao();
            progresso.setConcluido(false);

            progresso.marcarConcluido();

            assertThat(progresso.isConcluido()).isTrue();
        }
    }
}
