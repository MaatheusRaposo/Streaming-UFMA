package com.lab_prog.streaming.repositories;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lab_prog.streaming.model.entities.Midia;
import com.lab_prog.streaming.model.entities.StatusMidia;

public interface MidiaBuscaRepository extends JpaRepository<Midia, UUID> {

    @Query("select distinct g from Midia m join m.generos g where m.status = :status order by g")
    List<String> listarGeneros(@Param("status") StatusMidia status);

    @Query("select distinct m from Midia m join m.generos g where m.status = :status and g in :generos")
    List<Midia> buscarPorGeneros(@Param("status") StatusMidia status, @Param("generos") Collection<String> generos);

    List<Midia> findByStatusAndTituloContainingIgnoreCase(StatusMidia status, String titulo);

    List<Midia> findTop30ByStatusAndTituloStartingWithIgnoreCaseOrderByTituloAsc(StatusMidia status, String prefixo);

    List<Midia> findTop50ByStatusAndDataLancamentoIsNotNullOrderByDataLancamentoDesc(StatusMidia status);
}
