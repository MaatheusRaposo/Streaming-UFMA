package com.lab_prog.streaming.services;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lab_prog.streaming.dtos.progresso.AtualizarProgressoRequest;
import com.lab_prog.streaming.dtos.progresso.ProgressoVisualizacaoResponse;
import com.lab_prog.streaming.model.entities.ConteudoAssistivel;
import com.lab_prog.streaming.model.entities.Midia;
import com.lab_prog.streaming.model.entities.ProgressoVisualizacao;
import com.lab_prog.streaming.model.entities.ProgressoVisualizacaoId;
import com.lab_prog.streaming.model.entities.StatusMidia;
import com.lab_prog.streaming.model.entities.Usuario;
import com.lab_prog.streaming.repositories.MidiaRepository;
import com.lab_prog.streaming.repositories.ProgressoVisualizacaoRepository;
import com.lab_prog.streaming.repositories.UsuarioRepository;

@Service
public class ProgressoVisualizacaoService {

    private final ProgressoVisualizacaoRepository progressoRepository;
    private final UsuarioRepository usuarioRepository;
    private final MidiaRepository midiaRepository;

    public ProgressoVisualizacaoService(
            ProgressoVisualizacaoRepository progressoRepository,
            UsuarioRepository usuarioRepository,
            MidiaRepository midiaRepository) {
        this.progressoRepository = progressoRepository;
        this.usuarioRepository = usuarioRepository;
        this.midiaRepository = midiaRepository;
    }

    @Transactional
    public ProgressoVisualizacaoResponse atualizar(
            UUID usuarioId,
            UUID conteudoId,
            AtualizarProgressoRequest request) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        ConteudoAssistivel conteudo = buscarConteudoAtivo(conteudoId);

        if (request.tempoAssistidoSegundos() > conteudo.getDuracaoSegundos()) {
            throw new IllegalArgumentException(
                    "O tempo assistido não pode ultrapassar a duração do conteúdo");
        }

        ProgressoVisualizacaoId progressoId =
                new ProgressoVisualizacaoId(usuarioId, conteudoId);

        ProgressoVisualizacao progresso = progressoRepository.findById(progressoId)
                .orElseGet(() -> criarProgresso(progressoId, usuario, conteudo));

        int tempoAssistido = request.tempoAssistidoSegundos();
        progresso.atualizarProgresso(tempoAssistido);
        progresso.setUltimaVisualizacao(new Date());

        if (tempoAssistido >= conteudo.getDuracaoSegundos()) {
            progresso.marcarConcluido();
        } else {
            progresso.setConcluido(false);
        }

        return converterParaResponse(progressoRepository.save(progresso));
    }

    @Transactional(readOnly = true)
    public ProgressoVisualizacaoResponse buscar(UUID usuarioId, UUID conteudoId) {
        ProgressoVisualizacaoId progressoId =
                new ProgressoVisualizacaoId(usuarioId, conteudoId);

        ProgressoVisualizacao progresso = progressoRepository.findById(progressoId)
                .orElseThrow(() -> new IllegalArgumentException("Progresso não encontrado"));

        return converterParaResponse(progresso);
    }

    @Transactional(readOnly = true)
    public List<ProgressoVisualizacaoResponse> listarPorUsuario(UUID usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new IllegalArgumentException("Usuário não encontrado");
        }

        return progressoRepository.findByUserUserId(usuarioId)
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }

    @Transactional
    public void remover(UUID usuarioId, UUID conteudoId) {
        ProgressoVisualizacaoId progressoId =
                new ProgressoVisualizacaoId(usuarioId, conteudoId);

        if (!progressoRepository.existsById(progressoId)) {
            throw new IllegalArgumentException("Progresso não encontrado");
        }

        progressoRepository.deleteById(progressoId);
    }

    private ConteudoAssistivel buscarConteudoAtivo(UUID conteudoId) {
        Midia midia = midiaRepository.findById(conteudoId)
                .orElseThrow(() -> new IllegalArgumentException("Conteúdo não encontrado"));

        if (midia.getStatus() != StatusMidia.ATIVA) {
            throw new IllegalArgumentException("Conteúdo não encontrado");
        }

        if (!(midia instanceof ConteudoAssistivel conteudo)) {
            throw new IllegalArgumentException("A mídia não possui conteúdo assistível");
        }

        return conteudo;
    }

    private ProgressoVisualizacao criarProgresso(
            ProgressoVisualizacaoId progressoId,
            Usuario usuario,
            ConteudoAssistivel conteudo) {

        ProgressoVisualizacao progresso = new ProgressoVisualizacao();
        progresso.setPvId(progressoId);
        progresso.setUser(usuario);
        progresso.setConteudo(conteudo);
        return progresso;
    }

    private ProgressoVisualizacaoResponse converterParaResponse(
            ProgressoVisualizacao progresso) {

        return new ProgressoVisualizacaoResponse(
                progresso.getUser().getUserId(),
                progresso.getConteudo().getMidiaId(),
                progresso.getTempoAssistidoSegundos(),
                progresso.getUltimaVisualizacao(),
                progresso.isConcluido());
    }
}
