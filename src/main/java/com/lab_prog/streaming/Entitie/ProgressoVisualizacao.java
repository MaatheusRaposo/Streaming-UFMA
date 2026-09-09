package com.lab_prog.streaming;

import java.util.UUID;

import jakarta.persistence.Entity;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity 
@Getter 
@Setter 
@AllArgsConstructor 
public class ProgressoVisualizacao {
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
