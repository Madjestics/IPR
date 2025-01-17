package com.example.webfluxtest.service.impl;

import com.example.commontest.dto.Action;
import com.example.commontest.dto.MovieEvent;
import com.example.commontest.dto.WatchEvent;
import com.example.webfluxtest.entity.Movie;
import com.example.webfluxtest.exception.EntityNotFoundException;
import com.example.webfluxtest.exception.ValidationException;
import com.example.webfluxtest.mapper.MovieMapper;
import com.example.webfluxtest.repository.DirectorRepository;
import com.example.webfluxtest.repository.MovieRepository;
import com.example.webfluxtest.security.CustomPrincipal;
import com.example.webfluxtest.service.MessageService;
import com.example.webfluxtest.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Paths;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;
    private final DirectorRepository directorRepository;
    private final MessageService messageService;
    private final ResourceLoader resourceLoader;
    private final MovieMapper movieMapper;

    private static final String FILE_PATH_FORMAT = "file:///%s";
    private static final String BASE_FILE_PATH = new ClassPathResource("movies").getPath();

    public Flux<Movie> findAll() {
        return movieRepository.findAll();
    }

    public Mono<Movie> findById(Long id) {
        if (id == null) {
            return Mono.error(new ValidationException("ID не может быть пустым"));
        }
        return movieRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Фильм не найден")));
    }

    public Mono<Movie> add(Movie entity) {
        return validate(entity)
                .then(movieRepository.save(entity))
                .doOnSuccess(savedMovie -> messageService.sendMovieEvent(
                        new MovieEvent(Action.ADD, movieMapper.map(savedMovie))
                ))
                .onErrorResume(Mono::error);
    }

    public Mono<Movie> update(Movie entity, Long id) {
        return validate(entity)
                .then(movieRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Фильм для обновления не найден")))
                .flatMap(existing -> {
                        entity.setFilePath(existing.getFilePath());
                        return movieRepository.save(entity);
                })
                .doOnSuccess(movie -> messageService.sendMovieEvent(
                        new MovieEvent(Action.UPDATE, movieMapper.map(movie))
                ))
                .onErrorResume(Mono::error));
    }
    
    public Mono<Void> delete(Long id) {
        if (id == null) {
            return Mono.error(new ValidationException("Недопустимый ID"));
        }
        return movieRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Фильм для удаления не найден")))
                .flatMap(movie -> {
                    Mono<Void> res = movieRepository.deleteById(id);
                    messageService.sendMovieEvent(
                            new MovieEvent(Action.DELETE, movieMapper.map(movie))
                    );
                    return res;
                })
                .onErrorResume(Mono::error);
    }


    public Mono<Long> uploadMovie(Mono<FilePart> filePart, Long id) {
        return filePart.flatMap(file -> file.transferTo(Paths.get(BASE_FILE_PATH, "/", file.filename()))
                .then(Mono.just(Paths.get(BASE_FILE_PATH, "/", file.filename()).toAbsolutePath().toString())))
                .flatMap(filePath -> movieRepository.findById(id)
                        .flatMap(movie -> {
                            movie.setFilePath(filePath);
                            return movieRepository.save(movie);
                        }))
                .then(Mono.just(id))
                .onErrorResume(Mono::error);
    }

    public Mono<Resource> getForWatching(Long id) {
        return movieRepository.findById(id)
                .flatMap(movie-> ReactiveSecurityContextHolder.getContext()
                        .flatMap(context -> {
                            CustomPrincipal principal = (CustomPrincipal) context.getAuthentication().getPrincipal();
                            messageService.sendWatchEvent(new WatchEvent(movieMapper.map(movie), principal.getId()));
                            return Mono.just(movie);
                        }))
                .map(movie -> resourceLoader.getResource(String.format(FILE_PATH_FORMAT, movie.getFilePath())))
                .onErrorResume(Mono::error);
    }

    private Mono<Void> validate(Movie movie) {
        return Mono.just(movie)
                .flatMap(m -> {
                    if (m == null) {
                        return Mono.error(new ValidationException("Фильм не может быть null"));
                    }
                    return Mono.empty();
                })
                .then(validateLocalFields(movie))
                .then(validateDirector(movie.getDirectorId()));
    }

    private Mono<Void> validateLocalFields(Movie movie) {
        return Mono.defer(() -> {
            if (!StringUtils.hasText(movie.getTitle())) {
                return Mono.error(new ValidationException("Названия фильма должно быть не пустым"));
            }
            if (movie.getTitle().length() > 100) {
                return Mono.error(new ValidationException("Название фильма должно иметь длину менее 100 символов"));
            }
            if (movie.getYear() == null || movie.getYear() < 1900 || movie.getYear() > 2100) {
                return Mono.error(new ValidationException("Год выхода фильма должен быть между 1900 и 2100"));
            }
            if (movie.getDuration() == null || movie.getDuration().isBefore(LocalTime.MIN)) {
                return Mono.error(new ValidationException("Длительность фильма должна быть больше 0 секунд"));
            }
            if (movie.getRating() == null || movie.getRating() < 0 || movie.getRating() > 10) {
                return Mono.error(new ValidationException("Рейтинг фильма должен быть между 0 и 10 баллами"));
            }
            if (!StringUtils.hasText(movie.getGenre())) {
                return Mono.error(new ValidationException("Жанр фильма не может быть пустым"));
            }
            return Mono.empty();
        });
    }

    private Mono<Void> validateDirector(Long directorId) {
        if (directorId == null) {
            return Mono.error(new ValidationException("Режиссера фильма не может быть пустым"));
        }
        return directorRepository.findById(directorId)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Директор фильма не найден")))
                .then();
    }
}
