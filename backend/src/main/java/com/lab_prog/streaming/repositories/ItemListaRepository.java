package com.lab_prog.streaming.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lab_prog.streaming.model.entities.ItemLista;
import com.lab_prog.streaming.model.entities.ItemListaId;
import com.lab_prog.streaming.model.entities.TipoLista;

public interface ItemListaRepository extends JpaRepository<ItemLista, ItemListaId> {

    @Query("select i from ItemLista i where i.id.userId = :userId and i.id.tipo = :tipo order by i.adicionadoEm desc")
    List<ItemLista> listar(@Param("userId") UUID userId, @Param("tipo") TipoLista tipo);
}
