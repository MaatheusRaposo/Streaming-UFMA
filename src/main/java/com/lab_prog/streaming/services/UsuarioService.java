package com.lab_prog.streaming.services;

import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.lab_prog.streaming.dtos.usuario.CadastroUsuarioRequest;
import com.lab_prog.streaming.dtos.usuario.UsuarioResponse;
import com.lab_prog.streaming.model.entities.Usuario;
import com.lab_prog.streaming.repositories.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UsuarioResponse cadastrar(CadastroUsuarioRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("E-mail já cadastrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        usuario.setSenhaHash(passwordEncoder.encode(request.senha()));

        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        return converterParaResponse(usuarioSalvo);
    }

    public UsuarioResponse buscarPorId(UUID userId) {
        Usuario usuario = usuarioRepository.findById(userId) .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        return converterParaResponse(usuario);
    }

    public List<UsuarioResponse> listarTodos() {
        return usuarioRepository.findAll().stream().map(this::converterParaResponse).toList();
    }

    private UsuarioResponse converterParaResponse(Usuario usuario) {
        return new UsuarioResponse(usuario.getUserId(),usuario.getNome(),usuario.getEmail());
    }
}
