package com.lab_prog.streaming.model.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<UsuarioRepository, UUID> {
    
}
