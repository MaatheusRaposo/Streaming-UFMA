package com.lab_prog.streaming.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.lab_prog.streaming.dtos.episodio.AtualizarEpisodioRequest;
import com.lab_prog.streaming.dtos.episodio.CadastroEpisodioRequest;
import com.lab_prog.streaming.dtos.episodio.EpisodioResponse;
import com.lab_prog.streaming.model.entities.Episodio;
import com.lab_prog.streaming.model.entities.Serie;
import com.lab_prog.streaming.repositories.EpisodioRepository;
import com.lab_prog.streaming.repositories.SerieRepository;

@Service
public class EpisodioService {

    private final EpisodioRepository episodioRepository;
    private final SerieRepository serieRepository;

    public EpisodioService(EpisodioRepository episodioRepository, SerieRepository serieRepository) {
        this.episodioRepository = episodioRepository;
        this.serieRepository = serieRepository;
    }

    public EpisodioResponse cadastrar(UUID serieId, CadastroEpisodioRequest request) {
        Serie serie = serieRepository.findById(serieId)
                .orElseThrow(() -> new IllegalArgumentException("Série não encontrada"));

        Episodio episodio = new Episodio();
        episodio.setSerie(serie);
        copiarDados(request, episodio);

        Episodio episodioSalvo = episodioRepository.save(episodio);

        return converterParaResponse(episodioSalvo);
    }

    public EpisodioResponse buscarPorId(UUID episodioId) {
        Episodio episodio = buscarEntidadePorId(episodioId);
        return converterParaResponse(episodio);
    }

    public List<EpisodioResponse> listarPorSerie(UUID serieId) {
        return episodioRepository.findBySerieMidiaId(serieId)
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }

    public EpisodioResponse atualizar(UUID episodioId, AtualizarEpisodioRequest request) {
        Episodio episodio = buscarEntidadePorId(episodioId);
        copiarDados(request, episodio);

        Episodio episodioAtualizado = episodioRepository.save(episodio);

        return converterParaResponse(episodioAtualizado);
    }

    public void remover(UUID episodioId) {
        Episodio episodio = buscarEntidadePorId(episodioId);
        episodioRepository.delete(episodio);
    }

    private Episodio buscarEntidadePorId(UUID episodioId) {
        return episodioRepository.findById(episodioId).orElseThrow(() -> new IllegalArgumentException("Episódio não encontrado"));
    }

    private void copiarDados(CadastroEpisodioRequest request, Episodio episodio) {
        episodio.setTemporada(request.temporada());
        episodio.setNumero(request.numero());
        episodio.setTitulo(request.titulo());
        episodio.setDuracaoSegundos(request.duracaoSegundos());
        episodio.setUrl(request.urlVideo());
    }

    private void copiarDados(AtualizarEpisodioRequest request, Episodio episodio) {
        episodio.setTitulo(request.titulo());
        episodio.setDuracaoSegundos(request.duracaoSegundos());
        episodio.setUrl(request.urlVideo());
    }

    private EpisodioResponse converterParaResponse(Episodio episodio) {
        return new EpisodioResponse(
            episodio.getMidiaId(),
            episodio.getSerie().getMidiaId(),
            episodio.getTemporada(),
            episodio.getNumero(),
            episodio.getTitulo(),
            episodio.getDuracaoSegundos(),
            episodio.getUrl()
        );
    }
}