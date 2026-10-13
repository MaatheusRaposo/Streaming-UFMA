package com.lab_prog.streaming.model.entities;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SessaoReproducao {

    @Id
    @GeneratedValue
    private UUID sessaoId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private Usuario user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "conteudo_id")
    private ConteudoAssistivel conteudo;

    private Instant iniciadaEm;
    private Instant ultimaAtividade;
    private Instant encerradaEm;
}
