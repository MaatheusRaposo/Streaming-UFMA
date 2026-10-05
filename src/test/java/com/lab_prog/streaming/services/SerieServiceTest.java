package com.lab_prog.streaming.services;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.lab_prog.streaming.dtos.serie.AtualizarSerieRequest;
import com.lab_prog.streaming.dtos.serie.CadastroSerieRequest;
import com.lab_prog.streaming.dtos.serie.SerieResponse;
import com.lab_prog.streaming.model.entities.Serie;
import com.lab_prog.streaming.model.entities.StatusMidia;
import com.lab_prog.streaming.repositories.SerieRepository;

@DataJpaTest
@Import(SerieService.class)
class SerieServiceTest {

    @Autowired
    private SerieService serieService;

    @Autowired
    private SerieRepository serieRepository;

    @Test
    @DisplayName("Deve cadastrar uma série")
    void deveCadastrarSerie() {
        CadastroSerieRequest request = new CadastroSerieRequest(
                "Breaking Bad", "Um professor de química...", 2008, "poster", List.of("Drama"), 5);

        SerieResponse response = serieService.cadastrar(request);

        Serie serieSalva = serieRepository.findById(response.midiaId()).orElseThrow();
        assertThat(serieSalva.getTitulo()).isEqualTo("Breaking Bad");
        assertThat(serieSalva.getTotalTemporadas()).isEqualTo(5);
        assertThat(serieSalva.getStatus()).isEqualTo(StatusMidia.ATIVA);
    }

    @Test
    @DisplayName("Deve atualizar uma série existente")
    void deveAtualizarSerieExistente() {
        Serie serie = Serie.builder().titulo("Antiga").status(StatusMidia.ATIVA).build();
        Serie salva = serieRepository.save(serie);
        AtualizarSerieRequest request = new AtualizarSerieRequest(
                "Nova", "Sinopse nova", 2024, "novo-poster", List.of("Suspense"), 3);

        SerieResponse response = serieService.atualizar(salva.getMidiaId(), request);

        assertThat(response.titulo()).isEqualTo("Nova");
        assertThat(response.totalTemporadas()).isEqualTo(3);
        assertThat(response.status()).isEqualTo(StatusMidia.ATIVA);
    }

    @Test
    @DisplayName("Deve inativar uma série sem removê-la do banco")
    void deveInativarSerieSemRemoveLaDoBanco() {
        Serie serie = Serie.builder().status(StatusMidia.ATIVA).build();
        Serie salva = serieRepository.save(serie);

        serieService.remover(salva.getMidiaId());

        Optional<Serie> serieAtualizada = serieRepository.findById(salva.getMidiaId());
        assertThat(serieAtualizada).isPresent();
        assertThat(serieAtualizada.orElseThrow().getStatus()).isEqualTo(StatusMidia.INATIVA);
    }
}