package com.lab_prog.streaming.Model.entities;

import java.util.UUID;

import lombok.AllArgsConstructor;

import jakarta.persistence.Entity;
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
    private UUID userId;
    private String nome;
    private String email;
}
