package com.example.webfluxtest.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BaseService<T> {

    Flux<T> findAll();
    Mono<T> findById(Long id);
    Mono<T> add(T entity);
    Mono<T> update(T entity, Long id);
    Mono<Void> delete(Long id);
}
