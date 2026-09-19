package com.lab_prog.streaming.model.entities;

import java.util.UUID;

import lombok.AllArgsConstructor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank
    private String nome;

    @NotBlank
    @Email 
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @NotBlank 
    @Column(name = "senha_hash", nullable = false, length = 255)
    private String senhaHash;

}
