package com.lab_prog.streaming.repositories;
 
import java.util.List;
import java.util.UUID;
 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
 
import com.lab_prog.streaming.model.entities.ProgressoVisualizacao;
import com.lab_prog.streaming.model.entities.ProgressoVisualizacaoId;
 
public interface ProgressoVisualizacaoRepository extends JpaRepository<ProgressoVisualizacao, ProgressoVisualizacaoId> {
 
    List<ProgressoVisualizacao> findByUserUserId(UUID userId);
 
    @Query("select p from ProgressoVisualizacao p where p.pvId.userId = :userId order by p.ultimaVisualizacao desc")
    List<ProgressoVisualizacao> listarPorUsuario(@Param("userId") UUID userId);
 
    @Query("select p from ProgressoVisualizacao p where p.pvId.userId = :userId and p.concluido = false "
            + "and p.tempoAssistidoSegundos > 0 order by p.ultimaVisualizacao desc")
    List<ProgressoVisualizacao> listarEmAndamento(@Param("userId") UUID userId);
 
    @Modifying(clearAutomatically = true)
    @Query("delete from ProgressoVisualizacao p where p.pvId.userId = :userId")
    void apagarPorUsuario(@Param("userId") UUID userId);
}