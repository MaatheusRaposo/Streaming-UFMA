package com.lab_prog.streaming.model.entities;

import java.util.UUID;

import lombok.AllArgsConstructor;
import jakarta.annotation.Generated;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Setter 
@Getter 
@AllArgsConstructor 
@NoArgsConstructor 
public class Usuario {
    @Id 
    @GeneratedValue 
    private UUID userId;

    private String nome;
    private String email;
}
