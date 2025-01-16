package com.example.webfluxtest.security;

import com.example.webfluxtest.entity.User;
import com.example.webfluxtest.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SecurityService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Value("${jwt.secret}")
    private String secret;
    private static final long EXPIRATION_TIME = 3600;

    private TokenDetails generateTokenDetails(User user) {
        Map<String, Object> claims = new HashMap<>() {{
            put("role", user.getRole());
            put("username", user.getUsername());
        }};
        return generateTokenDetails(claims, user.getId().toString());
    }

    private TokenDetails generateTokenDetails(Map<String, Object> claims, String subject) {
        Date expirationDate = new Date(new Date().getTime() + EXPIRATION_TIME*1000L);
        return generateTokenDetails(expirationDate, claims, subject);
    }

    private TokenDetails generateTokenDetails(Date expirationDate, Map<String, Object> claims, String subject) {
        Date createdDate = new Date();
        String token = Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(createdDate)
                .expiration(expirationDate)
                .id(UUID.randomUUID().toString())
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)))
                .compact();

        return TokenDetails.builder()
                .token(token)
                .createdAt(createdDate)
                .expiredAt(expirationDate)
                .build();
    }

    public Mono<TokenDetails> authenticate(String username, String password) {
        return userService.findUserByUsername(username)
                .flatMap(user -> {
                    if (!user.isEnabled()) {
                        return Mono.error(new RuntimeException("Account disabled"));
                    }

                    if (!passwordEncoder.matches(password, user.getPassword())) {
                        return Mono.error(new RuntimeException("Invalid password"));
                    }

                    return Mono.just(generateTokenDetails(user).toBuilder()
                            .userId(user.getId())
                            .build());
                })
                .switchIfEmpty(Mono.error(new RuntimeException("Invalid username")));
    }
}
