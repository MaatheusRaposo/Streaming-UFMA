package com.lab_prog.streaming.repositories;

import com.lab_prog.streaming.model.entities.Episodio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EpisodioRepository
        extends JpaRepository<Episodio, UUID> {

    // Lista todos os episódios de uma série
    List<Episodio> findBySerieMidiaId(UUID serieId);

    // Lista os episódios de uma temporada específica
    List<Episodio> findBySerieMidiaIdAndTemporada(
        UUID serieId,
        int temporada
    );

    // Busca um episódio pelo número dentro de uma temporada
    List<Episodio> findBySerieMidiaIdAndTemporadaAndNumero(
        UUID serieId,
        int temporada,
        int numero
    );
}