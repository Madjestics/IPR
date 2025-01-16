package com.example.webfluxtest.config;

import com.example.webfluxtest.security.AuthenticationManager;
import com.example.webfluxtest.security.JwtHandler;
import com.example.webfluxtest.security.TokenServerAuthenticationConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
@EnableReactiveMethodSecurity
@EnableWebFluxSecurity
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String secret;

    private final String [] publicRoutes = {"/auth/register", "/auth/login", "/index.html"};

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity serverHttpSecurity, AuthenticationManager authenticationManager) {
        return serverHttpSecurity
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(HttpMethod.OPTIONS)
                        .permitAll()
                        .pathMatchers(publicRoutes)
                        .permitAll()
                        .anyExchange()
                        .authenticated())
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint((swe, e) -> {
                            log.error("SecurityWebFilterChain - unauthorized error: {}", e.getMessage());
                            return Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED));
                        })
                        .accessDeniedHandler((swe, e) -> {
                            log.error("SecurityWebFilterChain - access denied: {}", e.getMessage());
                            return Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN));
                        })
                )
                .addFilterBefore(authenticationWebFilter(authenticationManager), SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    private AuthenticationWebFilter authenticationWebFilter(AuthenticationManager authenticationManager) {
        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(authenticationManager);
        authenticationWebFilter.setServerAuthenticationConverter(
                new TokenServerAuthenticationConverter(
                        new JwtHandler(secret)
                )
        );
        authenticationWebFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/**"));
        return authenticationWebFilter;
    }
}
