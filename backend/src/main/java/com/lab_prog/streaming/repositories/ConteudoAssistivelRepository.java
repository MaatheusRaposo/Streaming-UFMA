package com.lab_prog.streaming.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lab_prog.streaming.model.entities.ConteudoAssistivel;

public interface ConteudoAssistivelRepository extends JpaRepository<ConteudoAssistivel, UUID> {
}
