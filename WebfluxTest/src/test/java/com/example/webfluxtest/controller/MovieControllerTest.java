package com.example.webfluxtest.controller;

import com.example.commontest.dto.MovieDto;
import com.example.webfluxtest.config.TestConfiguration;
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

import java.time.LocalTime;
import java.util.Objects;

@SpringBootTest
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@Import(TestConfiguration.class)
public class MovieControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private AuthUtils authUtils;

    private final static String CONTROLLER_PATH = "/api/movie";
    private final static String TITTLE = "Test title";

    private String token;
    private Long id;

    @BeforeEach
    public void addMovieTest() {
        token = authUtils.getAdminToken();

        MovieDto dto = new MovieDto();
        dto.setTitle(TITTLE);
        dto.setYear(2024);
        dto.setRating(8.2);
        dto.setDuration(LocalTime.of(2, 30, 20));
        dto.setDirectorId(1L);

        var movie = webTestClient.post()
                .uri(CONTROLLER_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token)
                .body(Mono.just(dto), MovieDto.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(MovieDto.class)
                .returnResult()
                .getResponseBody();

        Objects.requireNonNull(movie);

        Assertions.assertTrue(movie.getId() >= 2L);
        Assertions.assertEquals(movie.getTitle(), TITTLE);
        Assertions.assertEquals(movie.getYear(), 2024);

        id = movie.getId();
    }

    @Test
    public void testFindAll() {
        webTestClient.get()
                .uri(CONTROLLER_PATH)
                .header(HttpHeaders.AUTHORIZATION, token)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MovieDto.class)
                .hasSize(2);
    }

    @Test
    public void testFindById() {
        webTestClient.get()
                .uri(CONTROLLER_PATH + "/" + 1)
                .header(HttpHeaders.AUTHORIZATION, token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.title").isEqualTo("Test")
                .jsonPath("$.rating").isEqualTo(7.2);
    }

    @Test
    public void updateMovieTest() {
        MovieDto dto = new MovieDto();
        dto.setId(id);
        dto.setYear(2002);
        dto.setTitle("New title");
        dto.setRating(8.0);
        dto.setDuration(LocalTime.of(1, 50, 20));
        dto.setDirectorId(2L);
        webTestClient.patch()
                .uri(CONTROLLER_PATH + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(dto), MovieDto.class)
                .header(HttpHeaders.AUTHORIZATION, token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.year").isEqualTo(2002);
    }

    @AfterEach
    public void deleteMovieTest() {
        webTestClient.delete()
                .uri(CONTROLLER_PATH + "/" + id)
                .header(HttpHeaders.AUTHORIZATION, token)
                .exchange()
                .expectStatus().isOk();
    }
    
}
