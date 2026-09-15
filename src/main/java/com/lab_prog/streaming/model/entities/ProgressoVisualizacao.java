package com.lab_prog.streaming.model.entities;


import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;

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
    @EmbeddedId 
    private ProgressoVisualizacaoId pvId;
    
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
