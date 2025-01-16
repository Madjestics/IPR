package com.example.brokerstest.controller;

import com.example.brokerstest.dto.UserPreferencesDto;
import com.example.brokerstest.mapper.UserPreferencesMapper;
import com.example.brokerstest.service.RecommendationService;
import com.example.commontest.dto.MovieDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/preferences")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final UserPreferencesMapper userPreferencesMapper;

    @GetMapping("/recommendations")
    public Flux<MovieDto> getRecommendations(@RequestParam Long userId) {
        return recommendationService.generateRecommendations(userId);
    }

    @GetMapping("/watched")
    public Flux<MovieDto> getWatchedMovies(@RequestParam Long userId) {
        return recommendationService.getViewedMovies(userId);
    }

    @PostMapping
    public Mono<UserPreferencesDto> createUserPreferences(@RequestBody UserPreferencesDto preferences) {
        return recommendationService.createUserPreferences(userPreferencesMapper.map(preferences))
                .map(userPreferencesMapper::map);
    }
}
