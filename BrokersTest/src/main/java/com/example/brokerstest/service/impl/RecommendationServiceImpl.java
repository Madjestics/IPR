package com.example.brokerstest.service.impl;

import com.example.brokerstest.entity.UserPreferences;
import com.example.brokerstest.repository.UserPreferencesRepository;
import com.example.brokerstest.service.RecommendationService;
import com.example.commontest.dto.MovieDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationServiceImpl implements RecommendationService {

    private final UserPreferencesRepository preferencesRepository;

    @Override
    public Mono<UserPreferences> createUserPreferences(UserPreferences preferences) {
        return preferencesRepository.save(preferences);
    }

    public Flux<MovieDto> generateRecommendations(Long userId) {
        log.info(String.format("Пользователь %s хочет получить рекомендации по фильмам", userId));
        return preferencesRepository.findByUserId(userId)
                .flatMapMany(preferences -> {
                    List<MovieDto> recommendedMovies = preferences.getRecommendedMovies();
                    return Flux.fromIterable(recommendedMovies);
                });
    }

    @Override
    public Flux<MovieDto> getViewedMovies(Long userId) {
        log.info(String.format("Пользователь %s хочет получить просмотренные фильмы", userId));
        return preferencesRepository.findByUserId(userId)
                .flatMapMany(preferences -> {
                    List<MovieDto> viewedMovies = preferences.getViewedMovies();
                    return Flux.fromIterable(viewedMovies);
                });
    }
}
