package com.lab_prog.streaming.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lab_prog.streaming.model.entities.Midia;
import com.lab_prog.streaming.model.entities.StatusMidia;

public interface MidiaRepository extends JpaRepository<Midia, UUID> {
    List<Midia> findByTituloContainingIgnoreCase(String titulo);
    List<Midia> findByStatus(StatusMidia status);
    List<Midia> findByStatusAndTituloContainingIgnoreCase(StatusMidia status, String titulo);
}
