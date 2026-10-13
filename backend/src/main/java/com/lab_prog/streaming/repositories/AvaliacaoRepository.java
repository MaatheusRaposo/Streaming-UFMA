package com.lab_prog.streaming.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lab_prog.streaming.model.entities.Avaliacao;
import com.lab_prog.streaming.model.entities.AvaliacaoId;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, AvaliacaoId> {

    @Query("select avg(a.nota) from Avaliacao a where a.id.midiaId = :midiaId")
    Double mediaPorMidia(@Param("midiaId") UUID midiaId);

    @Query("select count(a) from Avaliacao a where a.id.midiaId = :midiaId")
    long totalPorMidia(@Param("midiaId") UUID midiaId);
}
