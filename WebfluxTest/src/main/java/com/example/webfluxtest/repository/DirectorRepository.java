package com.example.webfluxtest.repository;

import com.example.webfluxtest.entity.Director;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface DirectorRepository extends R2dbcRepository<Director, Long> {
}
