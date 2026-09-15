package com.lab_prog.streaming.model.entities;

import java.util.UUID;

import lombok.AllArgsConstructor;
import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
    
    @Column(unique = true)
    @NotNull 
    @NotEmpty 
    @NotBlank 
    private String email;
}
