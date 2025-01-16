package com.example.webfluxtest.controller;

import com.example.webfluxtest.config.TestConfiguration;
import com.example.webfluxtest.dto.AuthCredentialsDto;
import com.example.webfluxtest.dto.UserDtoTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@SpringBootTest
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@Import(TestConfiguration.class)
public class AuthControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    private final String CONTROLLER_PATH = "/auth";

    @BeforeEach
    public void testRegister() {
        UserDtoTest userDto = new UserDtoTest();
        userDto.setUsername("test_user");
        userDto.setPassword("test_pass");

        webTestClient.post()
                .uri(CONTROLLER_PATH+"/register")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(userDto), UserDtoTest.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(2L)
                .jsonPath("$.username").isEqualTo("test_user");

    }

    @Test
    public void testLogin() {
        AuthCredentialsDto credentialsDto = new AuthCredentialsDto();
        credentialsDto.setUsername("test_user");
        credentialsDto.setPassword("test_pass");

        webTestClient.post()
                .uri(CONTROLLER_PATH+"/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(credentialsDto), AuthCredentialsDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.user_id").isEqualTo(2L)
                .jsonPath("$.token").isNotEmpty();
    }
}
