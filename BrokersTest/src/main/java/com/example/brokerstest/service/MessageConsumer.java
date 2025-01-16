package com.example.brokerstest.service;

import com.example.brokerstest.repository.UserPreferencesRepository;
import com.example.commontest.dto.MovieDto;
import com.example.commontest.dto.MovieEvent;
import com.example.commontest.dto.WatchEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageConsumer {

    private final UserPreferencesRepository preferencesRepository;

    protected void handleMovieEvent(MovieEvent event) {
        switch (event.getAction()) {
            case ADD -> updatePreferencesForUsersWithNewMovie(event.getMovie()).subscribe();
            case UPDATE -> updatePreferencesForUsersWithUpdatedMovie(event.getMovie()).subscribe();
            case DELETE -> updatePreferencesForUsersWithDeletedMovie(event.getMovie()).subscribe();
            default -> log.error("Неподдерживаемый тип действия над фильмом");
        }
    }

    protected void handleWatchEvent(WatchEvent event) {
        log.info("Обработка события просмотра фильма: " + event);
        updateUserPreferencesWhenWatchedMovie(event).subscribe();
    }

    // Обновление пользовательских предпочтений с учетом нового фильма
    protected Mono<Void> updatePreferencesForUsersWithNewMovie(MovieDto movie) {
        log.info("Обновление пользовательских предпочтений с учетом нового фильма");
        return preferencesRepository.findAll()
                .flatMap(preferences -> {
                    if (preferences.getPreferredGenres().contains(movie.getGenre()) &&
                            !preferences.getViewedMovies().contains(movie)) {
                        // Добавляем фильм в список рекомендаций пользователя
                        if (!preferences.getRecommendedMovies().contains(movie)) {
                            preferences.getRecommendedMovies().add(movie);
                        }
                    }
                    return preferencesRepository.save(preferences);
                })
                .then();
    }

    protected Mono<Void> updatePreferencesForUsersWithUpdatedMovie(MovieDto movie) {
        log.info("Обновление пользовательских предпочтений с учетом новых данных фильма");
        return preferencesRepository.findAll()
                .flatMap(preferences -> {
                    var isMovieViewed = preferences.getViewedMovies().stream()
                            .anyMatch(existedMovie -> existedMovie.getId().equals(movie.getId()));

                    preferences.getRecommendedMovies().removeIf(existingMovie -> existingMovie.getId().equals(movie.getId()));
                    preferences.getViewedMovies().removeIf(existingMovie -> existingMovie.getId().equals(movie.getId()));

                    if (isMovieViewed) {
                        preferences.getViewedMovies().add(movie);
                    } else {
                        preferences.getRecommendedMovies().add(movie);
                    }
                    return preferencesRepository.save(preferences);
                })
                .then();
    }

    protected Mono<Void> updatePreferencesForUsersWithDeletedMovie(MovieDto movie) {
        log.info("Обновление пользовательских предпочтений с учетом удаления фильма");
        return preferencesRepository.findAll()
                .flatMap(preferences -> {
                    preferences.getRecommendedMovies().removeIf(existingMovie -> existingMovie.getId().equals(movie.getId()));
                    preferences.getViewedMovies().removeIf(existingMovie -> existingMovie.getId().equals(movie.getId()));
                    return preferencesRepository.save(preferences);
                })
                .then();
    }

    protected Mono<Void> updateUserPreferencesWhenWatchedMovie(WatchEvent event) {
        log.info("Обновление пользовательских предпочтений с учетом просмотра фильма");
        return preferencesRepository.findByUserId(event.getUserId())
                .flatMap(preferences -> {
                    MovieDto movie = event.getMovie();
                    if (!preferences.getViewedMovies().contains(movie)) {
                        preferences.getViewedMovies().add(movie);
                        String movieGenre = movie.getGenre();
                        List<MovieDto> movieWithSameGenre = preferences.getViewedMovies().stream()
                                .parallel()
                                .filter(movieDto -> movieDto.getGenre().equals(movieGenre))
                                .toList();
                        if (movieWithSameGenre.size()>=3 && !preferences.getPreferredGenres().contains(movieGenre)) {
                            preferences.getPreferredGenres().add(movieGenre);
                        }
                    }
                    preferences.getRecommendedMovies().remove(movie);
                    return preferencesRepository.save(preferences);
                })
                .then();
    }
}
