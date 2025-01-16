package com.example.webfluxtest.service.impl;

import com.example.webfluxtest.entity.Director;
import com.example.webfluxtest.exception.EntityNotFoundException;
import com.example.webfluxtest.exception.InternalServerException;
import com.example.webfluxtest.exception.ValidationException;
import com.example.webfluxtest.repository.DirectorRepository;
import com.example.webfluxtest.repository.MovieRepository;
import com.example.webfluxtest.service.DirectorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class DirectorServiceImpl implements DirectorService {
    private final DirectorRepository directorRepository;
    private final MovieRepository movieRepository;

    public Flux<Director> findAll() {
        return directorRepository.findAll();
    }

    public Mono<Director> findById(Long id) {
        if (id == null) {
            return Mono.error(new ValidationException("Недопустимый ID"));
        }
        return directorRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Режиссер не найден")));
    }

    public Mono<Director> add(Director director) {
        return validate(director)
                .then(directorRepository.save(director))
                .onErrorResume(Mono::error);
    }

    public Mono<Director> update(Director director, Long id) {
        if (id == null) {
            return Mono.error(new ValidationException("Недопустимый ID"));
        }
        return directorRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Режиссер для обновления информации не найден")))
                .flatMap(d -> validate(director)
                        .then(directorRepository.save(director))
                        .onErrorResume(Mono::error)
            )
                .onErrorResume(Mono::error);
    }

    public Mono<Void> delete(Long id) {
        if (id == null) {
            return Mono.error(new ValidationException("Недопустимый ID"));
        }
        return directorRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Режиссер для удаления не найден")))
                .flatMap(director -> movieRepository.findMoviesByDirectorId(id)
                            .collectList()
                            .flatMap(movies -> {
                                if (movies.isEmpty()) {
                                    return directorRepository.deleteById(id)
                                            .onErrorResume(Mono::error);
                                }
                                return Mono.error(new InternalServerException("У режиссера еще остались неудаленные фильмы"));
                            })
                )
                .onErrorResume(Mono::error);
    }

    private Mono<Void> validate(Director director) {
        if (director == null) {
            return Mono.error(new ValidationException("Режиссер не может быть null"));
        }
        if (director.getFio()==null || !StringUtils.hasText(director.getFio())) {
            return Mono.error(new ValidationException("ФИО режиссера не может быть пустым"));
        }
        return Mono.empty();
    }
}
