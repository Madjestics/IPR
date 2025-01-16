package com.example.webfluxtest.service;

import com.example.webfluxtest.entity.User;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<User> register(User user);
    Mono<User> findById(Long id);
    Mono<User> findUserByUsername(String username);
}
