package com.lab_prog.streaming.controllers.usuario;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lab_prog.streaming.dtos.usuario.CadastroUsuarioRequest;
import com.lab_prog.streaming.dtos.usuario.LoginRequest;
import com.lab_prog.streaming.dtos.usuario.LoginResponse;
import com.lab_prog.streaming.dtos.usuario.RefreshRequest;
import com.lab_prog.streaming.dtos.usuario.RefreshResponse;
import com.lab_prog.streaming.dtos.usuario.UsuarioResponse;
import com.lab_prog.streaming.services.UsuarioService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService){
        this.usuarioService = usuarioService;
    }

    @PostMapping("/register")
    public ResponseEntity<UsuarioResponse> cadastrar(
        @Valid @RequestBody CadastroUsuarioRequest request
    ) {
        UsuarioResponse user = usuarioService.cadastrar(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse userLogin = usuarioService.login(request);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(userLogin);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        LoginResponse userRefresh = usuarioService.refresh(request);

        return ResponseEntity.status(HttpStatus.OK).body(userRefresh);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public String getMethodName(@RequestParam String param) {
        return new String();
    }
    
    
}
