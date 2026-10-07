package com.lab_prog.streaming.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.lab_prog.streaming.dtos.midia.MidiaResponse;
import com.lab_prog.streaming.model.entities.Midia;
import com.lab_prog.streaming.model.entities.StatusMidia;
import com.lab_prog.streaming.repositories.MidiaRepository;

@Service
public class MidiaService {

    private final MidiaRepository midiaRepository;

    public MidiaService(MidiaRepository midiaRepository) {
        this.midiaRepository = midiaRepository;
    }

    public MidiaResponse buscarPorId(UUID midiaId) {
        Midia midia = midiaRepository.findById(midiaId)
                .orElseThrow(() -> new IllegalArgumentException("Mídia não encontrada"));

        if (midia.getStatus() != StatusMidia.ATIVA) {
            throw new IllegalArgumentException("Mídia não encontrada");
        }

        return converterParaResponse(midia);
    }

    public List<MidiaResponse> listar() {
        return midiaRepository.findByStatus(StatusMidia.ATIVA)
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }

    public List<MidiaResponse> buscarPorTitulo(String titulo) {
        return midiaRepository.findByStatusAndTituloContainingIgnoreCase(StatusMidia.ATIVA, titulo)
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }

    private MidiaResponse converterParaResponse(Midia midia) {
        return new MidiaResponse(
                midia.getMidiaId(),
                midia.getClass().getSimpleName(),
                midia.getTitulo(),
                midia.getSinopse(),
                midia.getAnoLancamento(),
                midia.getUrlPoster(),
                midia.getGeneros(),
                midia.getStatus()
        );
    }
}
