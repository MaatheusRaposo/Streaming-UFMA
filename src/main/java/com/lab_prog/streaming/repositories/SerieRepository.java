package com.lab_prog.streaming.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lab_prog.streaming.model.entities.Serie;

public interface SerieRepository extends JpaRepository<Serie, UUID> {
    
}
