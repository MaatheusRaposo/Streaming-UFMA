package com.lab_prog.streaming.model.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import com.lab_prog.streaming.model.entities.Episodio;
import com.lab_prog.streaming.model.entities.Filme;
import com.lab_prog.streaming.model.entities.ProgressoVisualizacao;
import com.lab_prog.streaming.model.entities.ProgressoVisualizacaoId;
import com.lab_prog.streaming.model.entities.Serie;
import com.lab_prog.streaming.model.entities.Usuario;

import com.lab_prog.streaming.repositories.UsuarioRepository;
import com.lab_prog.streaming.repositories.EpisodioRepository;
import com.lab_prog.streaming.repositories.FilmeRepository;
import com.lab_prog.streaming.repositories.SerieRepository;
import com.lab_prog.streaming.repositories.ProgressoVisualizacaoRepository;

@DataJpaTest
class RepositoriesIntegrationTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EpisodioRepository episodioRepository;

    @Autowired
    private FilmeRepository filmeRepository;

    @Autowired
    private SerieRepository serieRepository;

    @Autowired
    private ProgressoVisualizacaoRepository progressoVisualizacaoRepository;

    @Test
    @DisplayName("Deve persistir e buscar Usuário pelo ID")
    void deveSalvarEBuscarUsuario() {
        Usuario usuario = new Usuario();
        usuario.setNome("Marina");
        usuario.setEmail("marina@ufma.br");
        usuario.setSenhaHash("hash-de-teste");


        Usuario salvo = usuarioRepository.save(usuario);

        assertThat(salvo.getUserId()).isNotNull();
        Optional<Usuario> encontrado = usuarioRepository.findById(salvo.getUserId());
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNome()).isEqualTo("Marina");
        assertThat(encontrado.get().getEmail()).isEqualTo("marina@ufma.br");
    }

    @Test
    @DisplayName("Deve persistir e buscar Episódio pelo ID")
    void deveSalvarEBuscarEpisodio() {
        Episodio episodio = new Episodio();
        episodio.setTemporada(1);
        episodio.setNumero(1);
        episodio.setDuracaoSegundos(3600);
        episodio.setTitulo("Início de Tudo");
        episodio.setUrl("https://streaming.ufma.br/ep1.mp4");
        Serie serie = new Serie();
        serie.setTitulo("Série de Teste");
        serie.setTotalTemporadas(1);
        serie = serieRepository.save(serie);
        episodio.setSerie(serie);

        Episodio salvo = episodioRepository.save(episodio);

        assertThat(salvo.getMidiaId()).isNotNull();
        Optional<Episodio> encontrado = episodioRepository.findById(salvo.getMidiaId());
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getTitulo()).isEqualTo("Início de Tudo");
    }

    @Test
    @DisplayName("Deve persistir e buscar Filme com herança de Mídia")
    void deveSalvarEBuscarFilme() {
        Filme filme = Filme.builder()
                .titulo("O Poderoso Chefão")
                .sinopse("A história da família Corleone...")
                .anoLancamento(1972)
                .duracaoSegundos(10500)
                .generos(List.of("Crime", "Drama"))
                .build();

        Filme salvo = filmeRepository.save(filme);

        assertThat(salvo.getMidiaId()).isNotNull();
        Optional<Filme> encontrado = filmeRepository.findById(salvo.getMidiaId());
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getTitulo()).isEqualTo("O Poderoso Chefão");
        assertThat(encontrado.get().getDuracaoSegundos()).isEqualTo(10500);
    }

    @Test
    @DisplayName("Deve persistir e buscar Série com herança de Mídia")
    void deveSalvarEBuscarSerie() {
        Serie serie = Serie.builder()
                .titulo("Stranger Things")
                .sinopse("Um garoto desaparece em Hawkins...")
                .anoLancamento(2016)
                .totalTemporadas(4)
                .generos(List.of("Ficção Científica", "Terror"))
                .build();

        Serie salva = serieRepository.save(serie);

        assertThat(salva.getMidiaId()).isNotNull();
        Optional<Serie> encontrada = serieRepository.findById(salva.getMidiaId());
        assertThat(encontrada).isPresent();
        assertThat(encontrada.get().getTitulo()).isEqualTo("Stranger Things");
        assertThat(encontrada.get().getTotalTemporadas()).isEqualTo(4);
    }

    @Test
    @DisplayName("Deve persistir e buscar ProgressoVisualizacao com chave composta e relacionamentos")
    void deveSalvarEBuscarProgressoVisualizacao() {
        Usuario usuario = new Usuario();
        usuario.setNome("Lucas");
        usuario.setEmail("lucas@ufma.br");
        usuario.setSenhaHash("hash-de-teste");
        usuario = usuarioRepository.save(usuario);

        Serie serie = new Serie();
        serie.setTitulo("Série de Progresso");
        serie.setTotalTemporadas(1);
        serie = serieRepository.save(serie);

        Episodio episodio = new Episodio();
        episodio.setTemporada(1);
        episodio.setNumero(3);
        episodio.setDuracaoSegundos(2500);
        episodio.setTitulo("A Revelação");
        episodio.setUrl("https://streaming.ufma.br/ep3.mp4");
        episodio.setSerie(serie);
        episodio = episodioRepository.save(episodio);

        ProgressoVisualizacaoId pvId = new ProgressoVisualizacaoId(usuario.getUserId(), episodio.getMidiaId());
        ProgressoVisualizacao progresso = new ProgressoVisualizacao();
        progresso.setPvId(pvId);
        progresso.setUser(usuario);
        progresso.setConteudo(episodio);
        progresso.setTempoAssistidoSegundos(1500);
        progresso.setUltimaVisualizacao(new Date());
        progresso.setConcluido(false);

        ProgressoVisualizacao salvo = progressoVisualizacaoRepository.save(progresso);

        Optional<ProgressoVisualizacao> encontrado = progressoVisualizacaoRepository.findById(pvId);
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getTempoAssistidoSegundos()).isEqualTo(1500);
        assertThat(encontrado.get().getUser().getUserId()).isEqualTo(usuario.getUserId());
        assertThat(encontrado.get().getConteudo().getMidiaId()).isEqualTo(episodio.getMidiaId());
    }
}
