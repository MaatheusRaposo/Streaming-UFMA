package com.lab_prog.streaming.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lab_prog.streaming.model.entities.Midia;

public interface MidiaRepository extends JpaRepository<Midia, UUID> {
    
}
