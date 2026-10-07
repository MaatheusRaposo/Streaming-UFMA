package com.lab_prog.streaming.model.entities;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
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
public class Serie extends Midia{
    private int totalTemporadas;

    @OneToMany(mappedBy = "serie")
    private List<Episodio> episodios;
}
