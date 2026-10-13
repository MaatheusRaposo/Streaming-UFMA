package com.lab_prog.streaming.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.lab_prog.streaming.model.entities.Midia;
import com.lab_prog.streaming.model.entities.StatusMidia;

public interface MidiaRepository extends JpaRepository<Midia, UUID>, JpaSpecificationExecutor<Midia> {
    List<Midia> findByTituloContainingIgnoreCase(String titulo);
    List<Midia> findByStatus(StatusMidia status);
    List<Midia> findByStatusAndTituloContainingIgnoreCase(StatusMidia status, String titulo);
    List<Midia> findByStatus(StatusMidia status, Pageable pageable);
    List<Midia> findByStatusOrderByAnoLancamentoDesc(StatusMidia status, Pageable pageable);

    List<Midia> findByDestaqueTrueAndStatus( StatusMidia status ); 
    
    List<Midia> findByStatusOrderByVisualizacoesDesc(StatusMidia status);

    List<Midia> findByDataLancamentoBetweenAndStatusOrderByDataLancamentoDesc(
        LocalDate inicio,
        LocalDate fim,
        StatusMidia status
    );

    @Query("SELECT m FROM Midia m WHERE m.status = :status ORDER BY m.anoLancamento DESC")
    List<Midia> findTopLancamentos(StatusMidia status, Pageable pageable);
}
