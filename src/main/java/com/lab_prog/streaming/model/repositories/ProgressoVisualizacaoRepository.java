package com.lab_prog.streaming.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lab_prog.streaming.Model.entities.ProgressoVisualizacao;

public interface ProgressoVisualizacaoRepository extends JpaRepository<ProgressoVisualizacao, UUID> {
    
}
