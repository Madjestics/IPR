package com.example.webfluxtest.controller;

import com.example.webfluxtest.dto.AuthCredentialsDto;
import com.example.webfluxtest.dto.AuthResponseDto;
import com.example.webfluxtest.dto.UserDto;
import com.example.webfluxtest.entity.User;
import com.example.webfluxtest.mapper.UserMapper;
import com.example.webfluxtest.security.CustomPrincipal;
import com.example.webfluxtest.security.SecurityService;
import com.example.webfluxtest.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final SecurityService securityService;
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<UserDto> register(@RequestBody UserDto userDto) {
        User user = userMapper.map(userDto);
        return userService.register(user)
                .map(userMapper::map);
    }

    @PostMapping("/login")
    public Mono<AuthResponseDto> login (@RequestBody AuthCredentialsDto credentialsDto) {
        return securityService.authenticate(credentialsDto.getUsername(), credentialsDto.getPassword())
                .flatMap(tokenDetails -> Mono.just(
                        new AuthResponseDto(
                                tokenDetails.getUserId(),
                                tokenDetails.getToken(),
                                tokenDetails.getCreatedAt(),
                                tokenDetails.getExpiredAt()
                        )
                ));
    }

    @GetMapping("/info")
    public Mono<UserDto> getUserInfo(Authentication authentication) {
        CustomPrincipal principal = (CustomPrincipal) authentication.getPrincipal();
        return userService.findById(principal.getId())
                .map(userMapper::map);
    }
}
