package com.example.webfluxtest.service.impl;

import com.example.webfluxtest.entity.User;
import com.example.webfluxtest.entity.UserRole;
import com.example.webfluxtest.repository.UserRepository;
import com.example.webfluxtest.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Mono<User> register(User user) {
        return userRepository.save(
                user.toBuilder()
                        .password(passwordEncoder.encode(user.getPassword()))
                        .role(UserRole.USER)
                        .enabled(true)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()
        ).doOnSuccess(us -> log.info("Register user: {}", us));
    }

    public Mono<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Mono<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
