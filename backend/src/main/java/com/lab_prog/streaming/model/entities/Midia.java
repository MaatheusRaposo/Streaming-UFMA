package com.lab_prog.streaming.model.entities;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.ElementCollection;
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
@AllArgsConstructor 
@NoArgsConstructor 
public class Midia {
    @Id 
    @GeneratedValue 
    private UUID midiaId;

    private String titulo;
    private String sinopse;
    private String urlPoster;

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
