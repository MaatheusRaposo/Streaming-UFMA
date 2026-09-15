package com.lab_prog.streaming.model.entities;


import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;

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

    @ManyToOne 
    @MapsId 
    @JoinColumn(name="user_id")
    private Usuario user;

    @ManyToOne 
    @MapsId 
    @JoinColumn(name="ep_id")
    private Episodio ep;
    
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
