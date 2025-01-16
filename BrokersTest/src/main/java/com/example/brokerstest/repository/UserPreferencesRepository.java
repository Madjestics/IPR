package com.example.brokerstest.repository;

import com.example.brokerstest.entity.UserPreferences;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserPreferencesRepository extends ReactiveMongoRepository<UserPreferences, String> {
    Mono<UserPreferences> findByUserId(Long userId);
}
