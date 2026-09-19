package com.lab_prog.streaming.model.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@Entity 
@NoArgsConstructor 
@AllArgsConstructor 
@Getter 
@Setter 
@SuperBuilder
public class Episodio extends ConteudoAssistivel {
    private int temporada;
    private int numero;

    @ManyToOne
    @JoinColumn(name = "serie_id", nullable = false)
    private Serie serie;
}
