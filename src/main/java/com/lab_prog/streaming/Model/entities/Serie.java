package com.lab_prog.streaming.Model.entities;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity 
@Setter 
@Getter 
@SuperBuilder
public class Serie extends Midia{
    private int totalTemporadas;
}
