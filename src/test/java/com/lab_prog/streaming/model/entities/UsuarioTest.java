package com.lab_prog.streaming.model.entities;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class UsuarioTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Nested
    @DisplayName("Testes de Construtores e Métodos de Acesso (Getters/Setters)")
    class ConstrutoresEMetodosAcesso {

        @Test
        @DisplayName("Deve instanciar usuário com construtor padrão e atribuir valores via setters")
        void deveInstanciarComConstrutorPadrao() {
            Usuario usuario = new Usuario();
            UUID id = UUID.randomUUID();

            usuario.setUserId(id);
            usuario.setNome("João Silva");
            usuario.setEmail("joao@example.com");

            assertThat(usuario.getUserId()).isEqualTo(id);
            assertThat(usuario.getNome()).isEqualTo("João Silva");
            assertThat(usuario.getEmail()).isEqualTo("joao@example.com");
        }

        @Test
        @DisplayName("Deve instanciar usuário com todos os argumentos através do construtor")
        void deveInstanciarComTodosOsArgumentos() {
            UUID id = UUID.randomUUID();
            Usuario usuario = new Usuario(id, "Maria Oliveira", "maria@example.com");

            assertThat(usuario.getUserId()).isEqualTo(id);
            assertThat(usuario.getNome()).isEqualTo("Maria Oliveira");
            assertThat(usuario.getEmail()).isEqualTo("maria@example.com");
        }
    }

    @Nested
    @DisplayName("Testes de Validação Bean Validation (Constraints)")
    class ValidacoesBeanValidation {

        @Test
        @DisplayName("Deve passar na validação quando todos os dados forem válidos")
        void devePassarValidacaoComDadosValidos() {
            Usuario usuario = new Usuario(UUID.randomUUID(), "Carlos", "carlos@example.com");

            Set<ConstraintViolation<Usuario>> violacoes = validator.validate(usuario);

            assertThat(violacoes).isEmpty();
        }

        @Test
        @DisplayName("Deve falhar na validação quando o email for nulo (@NotNull)")
        void deveFalharQuandoEmailForNulo() {
            Usuario usuario = new Usuario(UUID.randomUUID(), "Carlos", null);

            Set<ConstraintViolation<Usuario>> violacoes = validator.validate(usuario);

            assertThat(violacoes).isNotEmpty();
            assertThat(violacoes).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
        }

        @Test
        @DisplayName("Deve falhar na validação quando o email for vazio (@NotEmpty)")
        void deveFalharQuandoEmailForVazio() {
            Usuario usuario = new Usuario(UUID.randomUUID(), "Carlos", "");

            Set<ConstraintViolation<Usuario>> violacoes = validator.validate(usuario);

            assertThat(violacoes).isNotEmpty();
            assertThat(violacoes).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
        }

        @Test
        @DisplayName("Deve falhar na validação quando o email contiver apenas espaços (@NotBlank)")
        void deveFalharQuandoEmailContiverApenasEspacos() {
            Usuario usuario = new Usuario(UUID.randomUUID(), "Carlos", "   ");

            Set<ConstraintViolation<Usuario>> violacoes = validator.validate(usuario);

            assertThat(violacoes).isNotEmpty();
            assertThat(violacoes).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
        }
    }
}

