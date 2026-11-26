package com.lab_prog.streaming.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ConteudoAssistivel extends Midia {
    private int duracaoSegundos;

    // Link do vídeo. Para o UIRA, é o embed do EduPlay.
    @Column(length = 2048)
    private String url;
}
