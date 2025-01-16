package com.example.brokerstest.service;

import com.example.brokerstest.entity.UserPreferences;
import com.example.commontest.dto.MovieDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RecommendationService {
    Mono<UserPreferences> createUserPreferences(UserPreferences preferences);

    Flux<MovieDto> generateRecommendations(Long userId);

    Flux<MovieDto> getViewedMovies(Long userId);
}
