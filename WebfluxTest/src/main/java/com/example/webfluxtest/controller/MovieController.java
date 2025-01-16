package com.example.webfluxtest.controller;

import com.example.commontest.dto.MovieDto;
import com.example.webfluxtest.mapper.MovieMapper;
import com.example.webfluxtest.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/movie")
public class MovieController {
    private final MovieService movieService;
    private final MovieMapper movieMapper;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Flux<MovieDto> findAll() {
        return movieService.findAll()
                .map(movieMapper::map);
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<MovieDto> findById(@PathVariable(name = "id") Long id) {
        return movieService.findById(id)
                .map(movieMapper::map);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovieDto> addMovie(@RequestBody MovieDto dto) {
        return movieService.add(movieMapper.map(dto))
                .onErrorResume(Mono::error)
                .map(movieMapper::map);
    }

    @PatchMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<MovieDto> updateMovie(@RequestBody MovieDto dto, @PathVariable Long id) {
        return movieService.update(movieMapper.map(dto), id)
                .onErrorResume(Mono::error)
                .map(movieMapper::map);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Void> deleteMovie(@PathVariable Long id) {
        return movieService.delete(id);
    }

    @PostMapping("upload/{id}")
    public Mono<Long> uploadMovie(@RequestPart("movie") Mono<FilePart> filePartMono, @PathVariable Long id) {
        return movieService.uploadMovie(filePartMono, id);
    }

    @GetMapping(value = "/watch/{id}", produces = "video/mp4")
    public Mono<Resource> watchMovie(@PathVariable Long id) {
        return movieService.getForWatching(id);
    }
}
