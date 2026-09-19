package com.lab_prog.streaming.dtos.usuario;
import java.util.UUID;


public record UsuarioResponse(
          UUID userId,
          String nome,
          String email
  ) {
}

