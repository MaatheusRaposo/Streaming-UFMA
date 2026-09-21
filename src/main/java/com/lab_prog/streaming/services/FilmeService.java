package com.lab_prog.streaming.services;

import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

import org.springframework.stereotype.Service;

import com.lab_prog.streaming.dtos.filme.AtualizarFilmeRequest;
import com.lab_prog.streaming.dtos.filme.CadastroFilmeRequest;
import com.lab_prog.streaming.dtos.filme.FilmeResponse;
import com.lab_prog.streaming.model.entities.Filme;
import com.lab_prog.streaming.model.entities.StatusMidia;
import com.lab_prog.streaming.repositories.FilmeRepository;

@Service
public class FilmeService {

    private final FilmeRepository filmeRepository;

    public FilmeService(FilmeRepository filmeRepository) {
        this.filmeRepository = filmeRepository;
    }

    public FilmeResponse cadastrar(CadastroFilmeRequest request) {
        Filme filme = new Filme();
        copiarDados(request, filme);

        Filme filmeSalvo = filmeRepository.save(filme);

        return converterParaResponse(filmeSalvo);
    }

    public FilmeResponse buscarPorId(UUID midiaId) {
        Filme filme = buscarEntidadePorId(midiaId);
        return converterParaResponse(filme);
    }

    public List<FilmeResponse> listar() {
        return filmeRepository.findAll().stream().map(this::converterParaResponse).toList();
    }

    public FilmeResponse atualizar(UUID midiaId, AtualizarFilmeRequest request) {
        Filme filme = buscarEntidadePorId(midiaId);
        copiarDados(request, filme);

        Filme filmeAtualizado = filmeRepository.save(filme);

        return converterParaResponse(filmeAtualizado);
    }

    public void remover(UUID midiaId) {
        Filme filme = buscarEntidadePorId(midiaId);
        filme.setStatus(StatusMidia.INATIVA);
        filmeRepository.save(filme);
    }

    private Filme buscarEntidadePorId(UUID midiaId) {
        return filmeRepository.findById(midiaId).orElseThrow(() -> new IllegalArgumentException("Filme não encontrado"));
    }

    private void copiarDados(CadastroFilmeRequest request, Filme filme) {
        filme.setTitulo(request.titulo());
        filme.setSinopse(request.sinopse());
        filme.setAnoLancamento(request.anoLancamento());
        filme.setUrlPoster(request.urlPoster());
        filme.setGeneros(new ArrayList<>(request.generos()));
        filme.setDuracaoSegundos(request.duracaoSegundos());
    }

    private void copiarDados(AtualizarFilmeRequest request, Filme filme) {
        filme.setTitulo(request.titulo());
        filme.setSinopse(request.sinopse());
        filme.setAnoLancamento(request.anoLancamento());
        filme.setUrlPoster(request.urlPoster());
        filme.setGeneros(new ArrayList<>(request.generos()));
        filme.setDuracaoSegundos(request.duracaoSegundos());
    }

    private FilmeResponse converterParaResponse(Filme filme) {
        return new FilmeResponse(
                filme.getMidiaId(),
                filme.getTitulo(),
                filme.getSinopse(),
                filme.getAnoLancamento(),
                filme.getUrlPoster(),
                filme.getGeneros(),
                filme.getDuracaoSegundos(),
                filme.getStatus()
        );
    }
}
