package com.example.webfluxtest.repository;

import com.example.webfluxtest.entity.Movie;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MovieRepository extends R2dbcRepository<Movie, Long> {
    Flux<Movie> findMoviesByDirectorId(Long directorId);
    Mono<Movie> findMovieByTitle(String title);
}
