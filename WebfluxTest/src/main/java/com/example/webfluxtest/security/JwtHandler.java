package com.example.webfluxtest.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import reactor.core.publisher.Mono;

import java.util.Date;

public class JwtHandler {
    @Value("${jwt.secret}")
    private String secret;

    public JwtHandler(String secret) {
        this.secret = secret;
    }

    public Mono<VerificationResult> check(String accessToken) {
        return Mono.just(verifyToken(accessToken))
                .onErrorResume(Mono::error);
    }

    private VerificationResult verifyToken(String token) {
        Claims claims = getClaimsFromToken(token);
        Date expirationDate = claims.getExpiration();
        if (expirationDate.before(new Date())) {
            throw new JwtException("Token expired");
        }

        return new VerificationResult(claims, token);
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public static class VerificationResult {
        public Claims claims;
        public String token;

        public VerificationResult(Claims claims, String token) {
            this.claims = claims;
            this.token = token;
        }
    }
}
