package com.lab_prog.streaming.dtos.usuario;

public record LoginResponse(String accessToken, String refreshToken, UsuarioResponse user) {
}
