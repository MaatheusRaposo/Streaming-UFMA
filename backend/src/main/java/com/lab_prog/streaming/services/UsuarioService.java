package com.lab_prog.streaming.services;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.lab_prog.streaming.dtos.usuario.AlterarSenhaRequest;
import com.lab_prog.streaming.dtos.usuario.AtualizarPerfilRequest;
import com.lab_prog.streaming.dtos.usuario.CadastroUsuarioRequest;
import com.lab_prog.streaming.dtos.usuario.LoginRequest;
import com.lab_prog.streaming.dtos.usuario.LoginResponse;
import com.lab_prog.streaming.dtos.usuario.RefreshRequest;
import com.lab_prog.streaming.dtos.usuario.UsuarioResponse;
import com.lab_prog.streaming.model.entities.Usuario;
import com.lab_prog.streaming.repositories.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional 
    public UsuarioResponse cadastrar(CadastroUsuarioRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
             throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Este e-mail já está cadastrado"
            );
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        usuario.setSenhaHash(passwordEncoder.encode(request.senha()));

        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        return converterParaResponse(usuarioSalvo);
    }

    public LoginResponse login(LoginRequest request){
        Usuario usuario = usuarioRepository.findByEmail(request.email())
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.UNAUTHORIZED, "Email não Cadastrado"
        ));

        if(!passwordEncoder.matches( request.senha(), usuario.getSenhaHash())){
            throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "Senha Incorreta"
            );
        }

        String accToken = jwtService.gerarAccessToken(request.email());
        String refToken = jwtService.gerarRefreshToken(request.email());
        UsuarioResponse usuarioResponse = converterParaResponse(usuario);

        return new LoginResponse(accToken, refToken, usuarioResponse);
    }

    public LoginResponse refresh(RefreshRequest request){
        String email = jwtService.validarRefreshToken(request.refreshToken());

        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "Credenciais inválidas"
            ));

        String newAccessToken = jwtService.gerarAccessToken(usuario.getEmail());

        return new LoginResponse(newAccessToken, request.refreshToken(), converterParaResponse(usuario));
    }

    public UsuarioResponse buscarPerfil(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Usuário não encontrado"));

        return new UsuarioResponse(
                usuario.getUserId(),
                usuario.getNome(),
                usuario.getEmail()
        );
    }

    @Transactional
    public UsuarioResponse atualizarPerfil(String emailAtual, AtualizarPerfilRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(emailAtual)
                .orElseThrow(() ->
                        new RuntimeException("Usuário não encontrado"));

        usuario.setNome(request.nome());

        // Atualiza o e-mail somente se ele tiver sido informado.
        if (request.email() != null
                && !request.email().isBlank()
                && !request.email().equals(usuario.getEmail())) {

            boolean emailEmUso = usuarioRepository
                    .findByEmail(request.email())
                    .isPresent();

            if (emailEmUso) {
                throw new RuntimeException(
                        "Este e-mail já está cadastrado");
            }

            usuario.setEmail(request.email());
        }

        usuarioRepository.save(usuario);

        return new UsuarioResponse(
                usuario.getUserId(),
                usuario.getNome(),
                usuario.getEmail()
        );
    }

    @Transactional 
    public UsuarioResponse alterarSenha(String email, AlterarSenhaRequest request){
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(() ->
                        new RuntimeException("Usuário não encontrado"));


         if(!passwordEncoder.matches( request.senha(), usuario.getSenhaHash())){
            throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "Senha Incorreta"
            );
         }

        usuario.setSenhaHash(passwordEncoder.encode(request.novaSenha()));

        usuarioRepository.save(usuario);

         return new UsuarioResponse(
            usuario.getUserId(),
            usuario.getNome(),
            usuario.getEmail()
         );
    }

    @Transactional
    public void excluirConta(String email) {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Usuário não encontrado"
                        ));

        usuarioRepository.delete(usuario);
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
