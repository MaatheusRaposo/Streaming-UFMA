package com.lab_prog.streaming;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity 
@Setter 
@Getter 
@AllArgsConstructor 
public class Filme extends Midia {
    private int duracaoSegundos;
}
