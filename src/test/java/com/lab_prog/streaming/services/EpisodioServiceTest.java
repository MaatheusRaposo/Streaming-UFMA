package com.lab_prog.streaming.services;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.lab_prog.streaming.dtos.episodio.AtualizarEpisodioRequest;
import com.lab_prog.streaming.dtos.episodio.CadastroEpisodioRequest;
import com.lab_prog.streaming.dtos.episodio.EpisodioResponse;
import com.lab_prog.streaming.model.entities.Episodio;
import com.lab_prog.streaming.model.entities.Serie;
import com.lab_prog.streaming.model.entities.StatusMidia;
import com.lab_prog.streaming.repositories.EpisodioRepository;
import com.lab_prog.streaming.repositories.SerieRepository;

@DataJpaTest
@Import(EpisodioService.class)
class EpisodioServiceTest {

    @Autowired
    private EpisodioService episodioService;

    @Autowired
    private EpisodioRepository episodioRepository;

    @Autowired
    private SerieRepository serieRepository;

    private Serie criarSerieSalva() {
        Serie serie = Serie.builder().titulo("Série Base").status(StatusMidia.ATIVA).build();
        return serieRepository.save(serie);
    }

    @Test
    @DisplayName("Deve cadastrar um episódio associado a uma série")
    void deveCadastrarEpisodio() {
        Serie serie = criarSerieSalva();
        CadastroEpisodioRequest request = new CadastroEpisodioRequest(1, 1, "Piloto", 1500, "url-video");

        EpisodioResponse response = episodioService.cadastrar(serie.getMidiaId(), request);

        Episodio episodioSalvo = episodioRepository.findById(response.episodioId()).orElseThrow();
        assertThat(episodioSalvo.getTitulo()).isEqualTo("Piloto");
        assertThat(episodioSalvo.getTemporada()).isEqualTo(1);
        assertThat(episodioSalvo.getNumero()).isEqualTo(1);
        assertThat(episodioSalvo.getUrl()).isEqualTo("url-video");
        assertThat(episodioSalvo.getSerie().getMidiaId()).isEqualTo(serie.getMidiaId());
    }

    @Test
    @DisplayName("Deve listar episódios de uma série")
    void deveListarEpisodiosPorSerie() {
        Serie serie = criarSerieSalva();
        Episodio ep1 = Episodio.builder().temporada(1).numero(1).titulo("Ep1").serie(serie).status(StatusMidia.ATIVA).build();
        Episodio ep2 = Episodio.builder().temporada(1).numero(2).titulo("Ep2").serie(serie).status(StatusMidia.ATIVA).build();
        episodioRepository.save(ep1);
        episodioRepository.save(ep2);

        List<EpisodioResponse> episodios = episodioService.listarPorSerie(serie.getMidiaId());

        assertThat(episodios)
                .hasSize(2)
                .extracting(EpisodioResponse::titulo)
                .containsExactlyInAnyOrder("Ep1", "Ep2");
    }

    @Test
    @DisplayName("Deve atualizar um episódio existente")
    void deveAtualizarEpisodioExistente() {
        Serie serie = criarSerieSalva();
        Episodio episodio = Episodio.builder().temporada(1).numero(1).titulo("Antigo").serie(serie).status(StatusMidia.ATIVA).build();
        Episodio salvo = episodioRepository.save(episodio);
        AtualizarEpisodioRequest request = new AtualizarEpisodioRequest("Novo título", 2000, "nova-url");

        EpisodioResponse response = episodioService.atualizar(salvo.getMidiaId(), request);

        assertThat(response.titulo()).isEqualTo("Novo título");
        assertThat(response.duracaoSegundos()).isEqualTo(2000);
        assertThat(response.urlVideo()).isEqualTo("nova-url");
    }

    @Test
    @DisplayName("Deve remover um episódio do banco")
    void deveRemoverEpisodio() {
        Serie serie = criarSerieSalva();
        Episodio episodio = Episodio.builder().temporada(1).numero(1).titulo("A remover").serie(serie).status(StatusMidia.ATIVA).build();
        Episodio salvo = episodioRepository.save(episodio);

        episodioService.remover(salvo.getMidiaId());

        Optional<Episodio> episodioRemovido = episodioRepository.findById(salvo.getMidiaId());
        assertThat(episodioRemovido).isEmpty();
    }
}