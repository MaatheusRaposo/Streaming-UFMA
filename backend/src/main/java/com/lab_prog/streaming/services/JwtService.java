
package com.lab_prog.streaming.services;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final SecretKey secretKey;

    private static final long ACCESS_TOKEN_EXPIRATION = 15 * 60 * 1000L;
    private static final long REFRESH_TOKEN_EXPIRATION = 7 * 24 * 60 * 60 * 1000L;

    public JwtService(@Value("${jwt.secret}") String secret) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String gerarAccessToken(String email) {
        return gerarToken(email, "access", ACCESS_TOKEN_EXPIRATION);
    }

    public String gerarRefreshToken(String email) {
        return gerarToken(email, "refresh", REFRESH_TOKEN_EXPIRATION);
    }

    private String gerarToken(String email, String tipo, long validade) {
        Date agora = new Date();
        Date expiracao = new Date(agora.getTime() + validade);

        return Jwts.builder()
                .subject(email)
                .claim("token_type", tipo)
                .issuedAt(agora)
                .expiration(expiracao)
                .signWith(secretKey)
                .compact();
    }

    public String validarAccessToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String tipo = claims.get("token_type", String.class);

        if (!"access".equals(tipo)) {
            throw new IllegalArgumentException(
                    "Token não é um access token"
            );
        }

        return claims.getSubject();
    }
    
    public String validarRefreshToken(String token) {
        var claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String tipo = claims.get("token_type", String.class);

        if (!"refresh".equals(tipo)) {
            throw new IllegalArgumentException("Token não é um refresh token");
        }

        return claims.getSubject();
    }
}