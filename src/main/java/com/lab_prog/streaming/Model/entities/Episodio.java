package com.lab_prog.streaming.Model.entities;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity 
@NoArgsConstructor 
@AllArgsConstructor 
@Getter 
@Setter 
public class Episodio {
    @Id 
    private UUID epId;
    private int temporada;
    private int numero;
    private int duracaoSegundos;
    private String titulo;
    private String url;
}
