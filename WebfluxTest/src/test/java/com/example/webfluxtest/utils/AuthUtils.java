package com.example.webfluxtest.utils;

import com.example.webfluxtest.security.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class AuthUtils {
    private final SecurityService securityService;
    private final static String BEARER_TOKEN = "Bearer ";

    public String getAdminToken() {
        return BEARER_TOKEN + Objects.requireNonNull(securityService.authenticate("admin", "admin")
                .block()).getToken();
    }
}
