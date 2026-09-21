package com.lab_prog.streaming.repositories;

import java.util.UUID;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lab_prog.streaming.model.entities.Episodio;

public interface EpisodioRepository extends JpaRepository<Episodio, UUID> {
    List<Episodio> findBySerieMidiaId(UUID serieId);
}