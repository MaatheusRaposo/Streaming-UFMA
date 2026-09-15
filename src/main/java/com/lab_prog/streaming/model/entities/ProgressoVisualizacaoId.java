package com.lab_prog.streaming.model.entities;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Embeddable 
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
public class ProgressoVisualizacaoId implements Serializable {
    private UUID userId;
    private UUID epID;
}
