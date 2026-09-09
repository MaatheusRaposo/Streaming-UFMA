package com.lab_prog.streaming.Entitie;

import java.util.UUID;

import lombok.AllArgsConstructor;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity 
@Setter 
@Getter 
@AllArgsConstructor 
public class Usuario {
    private UUID userId;
    private String nome;
    private String email;
}
