package com.lab_prog.streaming.model.entities;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Embeddable 
@Getter 
@Setter 
@EqualsAndHashCode
@NoArgsConstructor 
@AllArgsConstructor 
public class ProgressoVisualizacaoId implements Serializable {
    private UUID userId;
    private UUID conteudoId;
}
