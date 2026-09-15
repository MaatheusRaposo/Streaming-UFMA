package com.lab_prog.streaming.model.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lab_prog.streaming.model.entities.Filme;

public interface FilmeRepository extends JpaRepository<Filme, UUID>{
  
}
