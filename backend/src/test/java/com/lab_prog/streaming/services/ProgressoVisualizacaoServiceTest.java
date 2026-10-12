package com.lab_prog.streaming.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.lab_prog.streaming.dtos.progresso.AtualizarProgressoRequest;
import com.lab_prog.streaming.dtos.progresso.ProgressoVisualizacaoResponse;
import com.lab_prog.streaming.model.entities.Filme;
import com.lab_prog.streaming.model.entities.ProgressoVisualizacao;
import com.lab_prog.streaming.model.entities.Serie;
import com.lab_prog.streaming.model.entities.StatusMidia;
import com.lab_prog.streaming.model.entities.Usuario;
import com.lab_prog.streaming.repositories.MidiaRepository;
import com.lab_prog.streaming.repositories.ProgressoVisualizacaoRepository;
import com.lab_prog.streaming.repositories.UsuarioRepository;

@DataJpaTest
@Import(ProgressoVisualizacaoService.class)
class ProgressoVisualizacaoServiceTest {

    @Autowired
    private ProgressoVisualizacaoService progressoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MidiaRepository midiaRepository;

    @Autowired
    private ProgressoVisualizacaoRepository progressoRepository;

    @Test
    @DisplayName("Deve criar progresso e marcar como concluído ao atingir a duração")
    void deveCriarProgressoEConcluirConteudo() {
        Usuario usuario = criarUsuarioSalvo("ana@example.com");
        Filme filme = criarFilmeSalvo(3600);

        ProgressoVisualizacaoResponse response = progressoService.atualizar(
                usuario.getUserId(),
                filme.getMidiaId(),
                new AtualizarProgressoRequest(3600));

        assertThat(response.usuarioId()).isEqualTo(usuario.getUserId());
        assertThat(response.conteudoId()).isEqualTo(filme.getMidiaId());
        assertThat(response.tempoAssistidoSegundos()).isEqualTo(3600);
        assertThat(response.ultimaVisualizacao()).isNotNull();
        assertThat(response.concluido()).isTrue();
        assertThat(progressoRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Deve atualizar um progresso existente")
    void deveAtualizarProgressoExistente() {
        Usuario usuario = criarUsuarioSalvo("bruno@example.com");
        Filme filme = criarFilmeSalvo(5000);

        progressoService.atualizar(
                usuario.getUserId(),
                filme.getMidiaId(),
                new AtualizarProgressoRequest(1200));

        ProgressoVisualizacaoResponse response = progressoService.atualizar(
                usuario.getUserId(),
                filme.getMidiaId(),
                new AtualizarProgressoRequest(2500));

        assertThat(response.tempoAssistidoSegundos()).isEqualTo(2500);
        assertThat(response.concluido()).isFalse();
        assertThat(progressoRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Deve listar os progressos de um usuário")
    void deveListarProgressosDoUsuario() {
        Usuario usuario = criarUsuarioSalvo("carla@example.com");
        Filme filme1 = criarFilmeSalvo(3600);
        Filme filme2 = criarFilmeSalvo(7200);

        progressoService.atualizar(
                usuario.getUserId(),
                filme1.getMidiaId(),
                new AtualizarProgressoRequest(100));
        progressoService.atualizar(
                usuario.getUserId(),
                filme2.getMidiaId(),
                new AtualizarProgressoRequest(200));

        List<ProgressoVisualizacaoResponse> response =
                progressoService.listarPorUsuario(usuario.getUserId());

        assertThat(response).hasSize(2);
        assertThat(response)
                .extracting(ProgressoVisualizacaoResponse::conteudoId)
                .containsExactlyInAnyOrder(filme1.getMidiaId(), filme2.getMidiaId());
    }

    @Test
    @DisplayName("Não deve aceitar tempo maior que a duração do conteúdo")
    void naoDeveAceitarTempoMaiorQueDuracao() {
        Usuario usuario = criarUsuarioSalvo("diego@example.com");
        Filme filme = criarFilmeSalvo(3600);

        assertThatThrownBy(() -> progressoService.atualizar(
                usuario.getUserId(),
                filme.getMidiaId(),
                new AtualizarProgressoRequest(3601)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("O tempo assistido não pode ultrapassar a duração do conteúdo");
    }

    @Test
    @DisplayName("Não deve registrar progresso para uma série")
    void naoDeveRegistrarProgressoParaSerie() {
        Usuario usuario = criarUsuarioSalvo("eduardo@example.com");
        Serie serie = Serie.builder()
                .titulo("Série")
                .status(StatusMidia.ATIVA)
                .build();
        Serie serieSalva = (Serie) midiaRepository.save(serie);

        assertThatThrownBy(() -> progressoService.atualizar(
                usuario.getUserId(),
                serieSalva.getMidiaId(),
                new AtualizarProgressoRequest(100)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A mídia não possui conteúdo assistível");
    }

    @Test
    @DisplayName("Deve rejeitar usuário inexistente")
    void deveRejeitarUsuarioInexistente() {
        Filme filme = criarFilmeSalvo(3600);

        assertThatThrownBy(() -> progressoService.atualizar(
                java.util.UUID.randomUUID(),
                filme.getMidiaId(),
                new AtualizarProgressoRequest(100)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Usuário não encontrado");
    }

    @Test
    @DisplayName("Deve remover um progresso existente")
    void deveRemoverProgressoExistente() {
        Usuario usuario = criarUsuarioSalvo("fernanda@example.com");
        Filme filme = criarFilmeSalvo(3600);

        progressoService.atualizar(
                usuario.getUserId(),
                filme.getMidiaId(),
                new AtualizarProgressoRequest(100));

        progressoService.remover(usuario.getUserId(), filme.getMidiaId());

        assertThat(progressoRepository.count()).isZero();
    }

    private Usuario criarUsuarioSalvo(String email) {
        Usuario usuario = new Usuario();
        usuario.setNome("Usuário de teste");
        usuario.setEmail(email);
        usuario.setSenhaHash("hash-de-teste");
        return usuarioRepository.save(usuario);
    }

    private Filme criarFilmeSalvo(int duracaoSegundos) {
        Filme filme = Filme.builder()
                .titulo("Filme de teste")
                .status(StatusMidia.ATIVA)
                .duracaoSegundos(duracaoSegundos)
                .build();
        return (Filme) midiaRepository.save(filme);
    }
}
