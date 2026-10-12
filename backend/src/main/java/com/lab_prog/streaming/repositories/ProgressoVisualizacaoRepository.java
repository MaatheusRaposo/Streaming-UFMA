package com.lab_prog.streaming.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lab_prog.streaming.model.entities.ProgressoVisualizacao;
import com.lab_prog.streaming.model.entities.ProgressoVisualizacaoId;

public interface ProgressoVisualizacaoRepository extends JpaRepository<ProgressoVisualizacao, ProgressoVisualizacaoId> {
    List<ProgressoVisualizacao> findByUserUserId(UUID userId);
}
