package com.lab_prog.streaming.model.entities;

import java.time.Instant;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Avaliacao {

    @EmbeddedId
    private AvaliacaoId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private Usuario user;

    @ManyToOne
    @MapsId("midiaId")
    @JoinColumn(name = "midia_id")
    private Midia midia;

    private int nota;
    private Instant avaliadoEm;
}
