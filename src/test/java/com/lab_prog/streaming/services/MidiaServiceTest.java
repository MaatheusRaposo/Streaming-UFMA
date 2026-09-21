package com.lab_prog.streaming.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.lab_prog.streaming.dtos.midia.MidiaResponse;
import com.lab_prog.streaming.model.entities.Filme;
import com.lab_prog.streaming.model.entities.StatusMidia;
import com.lab_prog.streaming.repositories.MidiaRepository;

@DataJpaTest
@Import(MidiaService.class)
class MidiaServiceTest {

    @Autowired
    private MidiaService midiaService;

    @Autowired
    private MidiaRepository midiaRepository;

    @Test
    @DisplayName("Deve listar somente mídias ativas")
    void deveListarSomenteMidiasAtivas() {
        Filme ativa = Filme.builder().titulo("Filme ativo").status(StatusMidia.ATIVA).build();
        Filme inativa = Filme.builder().titulo("Filme inativo").status(StatusMidia.INATIVA).build();
        midiaRepository.save(ativa);
        midiaRepository.save(inativa);

        List<MidiaResponse> response = midiaService.listar();

        assertThat(response).extracting(MidiaResponse::titulo).containsExactly("Filme ativo");
    }

    @Test
    @DisplayName("Não deve retornar mídia inativa por ID")
    void naoDeveRetornarMidiaInativaPorId() {
        Filme filme = Filme.builder().status(StatusMidia.INATIVA).build();
        Filme salvo = (Filme) midiaRepository.save(filme);

        assertThatThrownBy(() -> midiaService.buscarPorId(salvo.getMidiaId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Mídia não encontrada");
    }

    @Test
    @DisplayName("Deve buscar mídia ativa pelo título")
    void deveBuscarMidiaAtivaPeloTitulo() {
        Filme filme = Filme.builder().titulo("Matrix").status(StatusMidia.ATIVA).build();
        midiaRepository.save(filme);

        List<MidiaResponse> response = midiaService.buscarPorTitulo("mat");

        assertThat(response).extracting(MidiaResponse::titulo).containsExactly("Matrix");
    }
}
