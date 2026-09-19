package com.lab_prog.streaming.model.entities;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ProgressoVisualizacaoIdTest {

    @Nested
    @DisplayName("Testes de Construtores e Métodos de Acesso")
    class ConstrutoresEMetodosAcesso {

        @Test
        @DisplayName("Deve instanciar chave composta com construtor padrão e atribuir via setters")
        void deveInstanciarComConstrutorPadraoESetters() {
            ProgressoVisualizacaoId id = new ProgressoVisualizacaoId();
            UUID userId = UUID.randomUUID();
            UUID conteudoId = UUID.randomUUID();

            id.setUserId(userId);
            id.setConteudoId(conteudoId);

            assertThat(id.getUserId()).isEqualTo(userId);
            assertThat(id.getConteudoId()).isEqualTo(conteudoId);
        }

        @Test
        @DisplayName("Deve instanciar chave composta com construtor completo")
        void deveInstanciarComTodosOsArgumentos() {
            UUID userId = UUID.randomUUID();
            UUID conteudoId = UUID.randomUUID();
            ProgressoVisualizacaoId id = new ProgressoVisualizacaoId(userId, conteudoId);

            assertThat(id.getUserId()).isEqualTo(userId);
            assertThat(id.getConteudoId()).isEqualTo(conteudoId);
        }
    }

    @Nested
    @DisplayName("Testes de Equals e HashCode")
    class EqualsEHashCode {

        @Test
        @DisplayName("Instâncias com mesmos valores devem ser iguais e possuir o mesmo hashCode")
        void deveSerIgualQuandoPossuirMesmosValores() {
            UUID userId = UUID.randomUUID();
            UUID conteudoId = UUID.randomUUID();

            ProgressoVisualizacaoId id1 = new ProgressoVisualizacaoId(userId, conteudoId);
            ProgressoVisualizacaoId id2 = new ProgressoVisualizacaoId(userId, conteudoId);

            assertThat(id1).isEqualTo(id2);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }

        @Test
        @DisplayName("Instâncias com valores diferentes não devem ser iguais")
        void naoDeveSerIgualQuandoPossuirValoresDiferentes() {
            UUID userId1 = UUID.randomUUID();
            UUID userId2 = UUID.randomUUID();
            UUID conteudoId = UUID.randomUUID();

            ProgressoVisualizacaoId id1 = new ProgressoVisualizacaoId(userId1, conteudoId);
            ProgressoVisualizacaoId id2 = new ProgressoVisualizacaoId(userId2, conteudoId);

            assertThat(id1).isNotEqualTo(id2);
            assertThat(id1.hashCode()).isNotEqualTo(id2.hashCode());
        }
    }
}
