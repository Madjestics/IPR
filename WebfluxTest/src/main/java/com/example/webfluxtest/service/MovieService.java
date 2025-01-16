package com.example.webfluxtest.service;

import com.example.webfluxtest.entity.Movie;
import org.springframework.core.io.Resource;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

public interface MovieService extends BaseService<Movie>  {
    Mono<Long> uploadMovie(Mono<FilePart> filePart, Long id);
    Mono<Resource> getForWatching(Long id);
}
