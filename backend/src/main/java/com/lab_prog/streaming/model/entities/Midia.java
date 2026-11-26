package com.lab_prog.streaming.model.entities;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity 
@Setter 
@Getter 
@SuperBuilder
@Inheritance(strategy = InheritanceType.JOINED)
@Table(uniqueConstraints = @UniqueConstraint(
        name = "uk_midia_origem_id_externo", columnNames = {"origem", "id_externo"}))
@AllArgsConstructor 
@NoArgsConstructor 
public class Midia {
    @Id 
    @GeneratedValue 
    private UUID midiaId;

    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String sinopse;

    @Column(length = 2048)
    private String urlPoster;

    // Plataforma de onde a mídia foi importada (ex.: "UIRA") e o id dela lá. Nulos quando cadastrada aqui.
    @Column(length = 30)
    private String origem;

    @Column(length = 100)
    private String idExterno;

    // Hash dos dados de origem, para reimportar só o que mudou
    @Column(length = 64)
    private String hashOrigem;

    private Instant sincronizadoEm;

    @Column(length = 10)
    private String classificacaoIndicativa;

    @Column(length = 10)
    private String uf;

    @Column(columnDefinition = "TEXT")
    private String tags;

    // Ex.: {"direcao": "...", "roteiro": "...", "fotografia": "..."}
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, String> fichaTecnica;

    @Column(nullable = false)
    @lombok.Builder.Default
    private boolean destaque = false;

    private LocalDate dataLancamento;

    @Column(nullable = false)
    @lombok.Builder.Default
    private long visualizacoes = 0L;

    @ElementCollection 
    private List<String> generos;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @lombok.Builder.Default
    private StatusMidia status = StatusMidia.ATIVA;
}
