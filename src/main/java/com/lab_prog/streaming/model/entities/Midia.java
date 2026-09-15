package com.lab_prog.streaming.model.entities;

import java.util.List;
import java.util.UUID;

import jakarta.annotation.Generated;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity 
@Setter 
@Getter 
@SuperBuilder
@Inheritance(strategy = InheritanceType.JOINED)
public class Midia {
    @Id 
    @GeneratedValue 
    private UUID midiaId;

    @ManyToOne 
    @MapsId 
    @JoinColumn(name="user_id")
    private Usuario user;

    @ManyToOne 
    @MapsId 
    @JoinColumn(name="ep_id")
    private Episodio ep;

    private String titulo;
    private String sinopse;
    private int anoLancamento;
    private String urlPoster;
    
    @ElementCollection 
    private List<String> generos;
}
