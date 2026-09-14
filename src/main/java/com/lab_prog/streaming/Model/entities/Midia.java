package com.lab_prog.streaming.Model.entities;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity 
@Setter 
@Getter 
@SuperBuilder
public class Midia {
    @Id 
    private UUID midiaId;
    private String titulo;
    private String sinopse;
    private int anoLancamento;
    private String urlPoster;
    private List<String> generos;
}
