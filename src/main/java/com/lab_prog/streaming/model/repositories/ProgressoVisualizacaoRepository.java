package com.lab_prog.streaming.model.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lab_prog.streaming.model.entities.ProgressoVisualizacao;
import com.lab_prog.streaming.model.entities.ProgressoVisualizacaoId;

public interface ProgressoVisualizacaoRepository extends JpaRepository<ProgressoVisualizacao, ProgressoVisualizacaoId> {
    
}
