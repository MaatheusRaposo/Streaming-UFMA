package com.lab_prog.streaming.repositories;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lab_prog.streaming.model.entities.Filme;

public interface FilmeRepository extends JpaRepository<Filme, UUID>{

    List<Filme> findByOrigemAndIdExternoIn(String origem, Collection<String> idsExternos);
}
