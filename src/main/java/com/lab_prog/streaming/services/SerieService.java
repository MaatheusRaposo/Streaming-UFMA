package com.lab_prog.streaming.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.lab_prog.streaming.dtos.serie.AtualizarSerieRequest;
import com.lab_prog.streaming.dtos.serie.CadastroSerieRequest;
import com.lab_prog.streaming.dtos.serie.SerieResponse;
import com.lab_prog.streaming.model.entities.Serie;
import com.lab_prog.streaming.model.entities.StatusMidia;
import com.lab_prog.streaming.repositories.SerieRepository;

@Service
public class SerieService {

    private final SerieRepository serieRepository;

    public SerieService(SerieRepository serieRepository) {
        this.serieRepository = serieRepository;
    }

    public SerieResponse cadastrar(CadastroSerieRequest request) {
        Serie serie = new Serie();
        copiarDados(request, serie);

        Serie serieSalva = serieRepository.save(serie);

        return converterParaResponse(serieSalva);
    }

    public SerieResponse buscarPorId(UUID midiaId) {
        Serie serie = buscarEntidadePorId(midiaId);
        return converterParaResponse(serie);
    }

    public List<SerieResponse> listar() {
        return serieRepository.findAll().stream().map(this::converterParaResponse).toList();
    }

    public SerieResponse atualizar(UUID midiaId, AtualizarSerieRequest request) {
        Serie serie = buscarEntidadePorId(midiaId);
        copiarDados(request, serie);

        Serie serieAtualizada = serieRepository.save(serie);

        return converterParaResponse(serieAtualizada);
    }

    public void remover(UUID midiaId) {
        Serie serie = buscarEntidadePorId(midiaId);
        serie.setStatus(StatusMidia.INATIVA);
        serieRepository.save(serie);
    }

    private Serie buscarEntidadePorId(UUID midiaId) {
        return serieRepository.findById(midiaId).orElseThrow(() -> new IllegalArgumentException("Série não encontrada"));
    }

    private void copiarDados(CadastroSerieRequest request, Serie serie) {
        serie.setTitulo(request.titulo());
        serie.setSinopse(request.sinopse());
        serie.setAnoLancamento(request.anoLancamento());
        serie.setUrlPoster(request.urlPoster());
        serie.setGeneros(new ArrayList<>(request.generos()));
        serie.setTotalTemporadas(request.totalTemporadas());
    }

    private void copiarDados(AtualizarSerieRequest request, Serie serie) {
        serie.setTitulo(request.titulo());
        serie.setSinopse(request.sinopse());
        serie.setAnoLancamento(request.anoLancamento());
        serie.setUrlPoster(request.urlPoster());
        serie.setGeneros(new ArrayList<>(request.generos()));
        serie.setTotalTemporadas(request.totalTemporadas());
    }

    private SerieResponse converterParaResponse(Serie serie) {
        return new SerieResponse(
                serie.getMidiaId(),
                serie.getTitulo(),
                serie.getSinopse(),
                serie.getAnoLancamento(),
                serie.getUrlPoster(),
                serie.getGeneros(),
                serie.getTotalTemporadas(),
                serie.getStatus()
        );
    }
}