package com.lab_prog.streaming.Model.entities;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Getter 
@Setter 
@AllArgsConstructor 
@NoArgsConstructor 
public class ProgressoVisualizacao {
    @Id 
    private UUID userId;
    private UUID epID;
    
    private int tempoAssistidoSegundos;
    private Date ultimaVisualizacao;
    private boolean concluido;

    public void atualizarProgresso(int segundos){
        setTempoAssistidoSegundos(segundos);
    }

    public void marcarConcluido(){
        setConcluido(true);
    }
}
