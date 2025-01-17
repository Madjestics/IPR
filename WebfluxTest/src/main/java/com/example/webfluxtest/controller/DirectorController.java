package com.example.webfluxtest.controller;

import com.example.webfluxtest.dto.DirectorDto;
import com.example.webfluxtest.mapper.DirectorMapper;
import com.example.webfluxtest.service.DirectorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/director")
public class DirectorController {
    private final DirectorService directorService;
    private final DirectorMapper directorMapper;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public Flux<DirectorDto> findAll() {
        return directorService.findAll()
                .map(directorMapper::map);
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public Mono<DirectorDto> findById(@PathVariable(name = "id") Long id) {
        return directorService.findById(id)
                .map(directorMapper::map);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ADMIN')")
    public Mono<DirectorDto> addDirector(@RequestBody DirectorDto dto) {
        return directorService.add(directorMapper.map(dto))
                .onErrorResume(Mono::error)
                .map(directorMapper::map);
    }

    @PatchMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN')")
    public Mono<DirectorDto> updateDirector(@RequestBody DirectorDto dto, @PathVariable(name = "id") Long id) {
        return directorService.update(directorMapper.map(dto), id)
                .onErrorResume(Mono::error)
                .map(directorMapper::map);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN')")
    public Mono<Void> deleteDirector(@PathVariable(name = "id") Long id) {
        return directorService.delete(id);
    }
}
