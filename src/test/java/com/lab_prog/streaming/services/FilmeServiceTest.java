package com.lab_prog.streaming.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.lab_prog.streaming.dtos.filme.AtualizarFilmeRequest;
import com.lab_prog.streaming.dtos.filme.CadastroFilmeRequest;
import com.lab_prog.streaming.dtos.filme.FilmeResponse;
import com.lab_prog.streaming.model.entities.Filme;
import com.lab_prog.streaming.model.entities.StatusMidia;
import com.lab_prog.streaming.repositories.FilmeRepository;

@DataJpaTest
@Import(FilmeService.class)
class FilmeServiceTest {

    @Autowired
    private FilmeService filmeService;

    @Autowired
    private FilmeRepository filmeRepository;

    @Test
    @DisplayName("Deve cadastrar um filme")
    void deveCadastrarFilme() {
        CadastroFilmeRequest request = new CadastroFilmeRequest("Matrix", "Uma história", 1999, "poster", List.of("Ação"), 8160);

        FilmeResponse response = filmeService.cadastrar(request);

        Filme filmeSalvo = filmeRepository.findById(response.midiaId()).orElseThrow();
        assertThat(filmeSalvo.getTitulo()).isEqualTo("Matrix");
        assertThat(filmeSalvo.getDuracaoSegundos()).isEqualTo(8160);
        assertThat(filmeSalvo.getStatus()).isEqualTo(StatusMidia.ATIVA);
    }

    @Test
    @DisplayName("Deve atualizar um filme existente")
    void deveAtualizarFilmeExistente() {
        Filme filme = Filme.builder().titulo("Antigo").status(StatusMidia.ATIVA).build();
        Filme salvo = filmeRepository.save(filme);
        AtualizarFilmeRequest request = new AtualizarFilmeRequest("Novo", "Sinopse nova", 2024, "novo-poster", List.of("Drama"), 7200);

        FilmeResponse response = filmeService.atualizar(salvo.getMidiaId(), request);

        assertThat(response.titulo()).isEqualTo("Novo");
        assertThat(response.sinopse()).isEqualTo("Sinopse nova");
        assertThat(response.status()).isEqualTo(StatusMidia.ATIVA);
    }

    @Test
    @DisplayName("Deve inativar um filme sem removê-lo do banco")
    void deveInativarFilmeSemRemoveLoDoBanco() {
        Filme filme = Filme.builder().status(StatusMidia.ATIVA).build();
        Filme salvo = filmeRepository.save(filme);

        filmeService.remover(salvo.getMidiaId());

        Optional<Filme> filmeAtualizado = filmeRepository.findById(salvo.getMidiaId());
        assertThat(filmeAtualizado).isPresent();
        assertThat(filmeAtualizado.orElseThrow().getStatus()).isEqualTo(StatusMidia.INATIVA);
    }
}
