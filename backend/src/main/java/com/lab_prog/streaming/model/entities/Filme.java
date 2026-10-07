package com.lab_prog.streaming.model.entities;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity 
@Setter 
@Getter 
@SuperBuilder
@NoArgsConstructor 
public class Filme extends ConteudoAssistivel {
}
