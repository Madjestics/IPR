package com.example.webfluxtest.security;

import com.example.webfluxtest.entity.User;
import com.example.webfluxtest.exception.AuthException;
import com.example.webfluxtest.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final UserService userService;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        CustomPrincipal principal = (CustomPrincipal) authentication.getPrincipal();
        return userService.findById(principal.getId())
                .filter(User::isEnabled)
                .switchIfEmpty(Mono.error(new AuthException("Пользователь заблокирован")))
                .map(user -> authentication);
    }
}
