package com.lab_prog.streaming.repositories;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lab_prog.streaming.model.entities.SessaoReproducao;

public interface SessaoReproducaoRepository extends JpaRepository<SessaoReproducao, UUID> {

    @Query("select s from SessaoReproducao s where s.user.userId = :userId "
            + "and s.encerradaEm is null and s.ultimaAtividade >= :limite")
    List<SessaoReproducao> listarAtivas(@Param("userId") UUID userId, @Param("limite") Instant limite);
}
