package com.example.webfluxtest.controller;

import com.example.webfluxtest.config.TestConfiguration;
import com.example.webfluxtest.dto.DirectorDto;
import com.example.webfluxtest.entity.Director;
import com.example.webfluxtest.utils.AuthUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Objects;


@SpringBootTest
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@Import(TestConfiguration.class)
public class DirectorControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private AuthUtils authUtils;

    private final String CONTROLLER_PATH = "/api/director";
    private String token;
    private Long id;

    @BeforeEach
    public void addDirectorTest() {
        token = authUtils.getAdminToken();

        String fio = "Тест Создания Режиссера";

        DirectorDto dto = new DirectorDto();
        dto.setFio(fio);
        var director = webTestClient.post()
                .uri(CONTROLLER_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token)
                .body(Mono.just(dto), DirectorDto.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(DirectorDto.class)
                .returnResult()
                .getResponseBody();

        Objects.requireNonNull(director);

        Assertions.assertTrue(director.getId() >= 3L);
        Assertions.assertEquals(director.getFio(), fio);

        id = director.getId();
    }

    @Test
    public void testFindAll() {
        webTestClient.get()
                .uri(CONTROLLER_PATH)
                .header(HttpHeaders.AUTHORIZATION, token)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Director.class)
                .hasSize(3);
    }

    @Test
    public void testFindById() {
        webTestClient.get()
                .uri(CONTROLLER_PATH+"/1")
                .header(HttpHeaders.AUTHORIZATION, token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.fio").isEqualTo("Тестов1 Тест1 Тест1");
    }

    @Test
    public void updateDirectorTest() {
        DirectorDto dto = new DirectorDto();
        dto.setId(id);
        dto.setFio("Тест Обновления Режиссера");
        webTestClient.patch()
                .uri(CONTROLLER_PATH + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(dto), DirectorDto.class)
                .header(HttpHeaders.AUTHORIZATION, token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.fio").isEqualTo("Тест Обновления Режиссера");
    }

    @AfterEach
    public void deleteDirectorTest() {
        webTestClient.delete()
                .uri(CONTROLLER_PATH + "/" + id.toString())
                .header(HttpHeaders.AUTHORIZATION, token)
                .exchange()
                .expectStatus().isOk();
    }
}
