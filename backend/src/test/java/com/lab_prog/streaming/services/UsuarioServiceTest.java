package com.lab_prog.streaming.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.lab_prog.streaming.config.PasswordConfig;
import com.lab_prog.streaming.dtos.usuario.CadastroUsuarioRequest;
import com.lab_prog.streaming.dtos.usuario.UsuarioResponse;
import com.lab_prog.streaming.model.entities.Usuario;
import com.lab_prog.streaming.repositories.UsuarioRepository;

@DataJpaTest
@Import({UsuarioService.class, PasswordConfig.class})
class UsuarioServiceTest {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Deve cadastrar usuário usando o hash da senha")
    void deveCadastrarUsuarioUsandoHashDaSenha() {
        CadastroUsuarioRequest request = new CadastroUsuarioRequest("Maria", "maria@example.com", "senha123");

        UsuarioResponse response = usuarioService.cadastrar(request);

        Usuario usuarioSalvo = usuarioRepository.findById(response.userId()).orElseThrow();
        assertThat(response.nome()).isEqualTo("Maria");
        assertThat(response.email()).isEqualTo("maria@example.com");
        assertThat(usuarioSalvo.getSenhaHash()).isNotEqualTo("senha123");
        assertThat(passwordEncoder.matches("senha123", usuarioSalvo.getSenhaHash())).isTrue();
    }

    @Test
    @DisplayName("Deve rejeitar cadastro com e-mail duplicado")
    void deveRejeitarCadastroComEmailDuplicado() {
        CadastroUsuarioRequest request = new CadastroUsuarioRequest("Maria", "maria@example.com", "senha123");
        usuarioService.cadastrar(request);

        assertThatThrownBy(() -> usuarioService.cadastrar(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("E-mail já cadastrado");
    }

    @Test
    @DisplayName("Deve buscar usuário por ID")
    void deveBuscarUsuarioPorId() {
        Usuario usuario = new Usuario();
        usuario.setNome("João");
        usuario.setEmail("joao@example.com");
        usuario.setSenhaHash("hash-de-teste");
        Usuario salvo = usuarioRepository.save(usuario);

        UsuarioResponse response = usuarioService.buscarPorId(salvo.getUserId());

        assertThat(response.userId()).isEqualTo(salvo.getUserId());
        assertThat(response.nome()).isEqualTo("João");
    }
}
