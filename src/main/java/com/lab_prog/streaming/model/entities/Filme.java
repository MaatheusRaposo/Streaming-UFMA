package com.lab_prog.streaming.model.entities;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity 
@Setter 
@Getter 
@SuperBuilder
@NoArgsConstructor 
@AllArgsConstructor
public class Filme extends Midia {
    private int duracaoSegundos;
}
