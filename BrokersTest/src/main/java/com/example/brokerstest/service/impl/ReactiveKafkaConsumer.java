package com.example.brokerstest.service.impl;

import com.example.brokerstest.repository.UserPreferencesRepository;
import com.example.brokerstest.service.MessageConsumer;
import com.example.commontest.dto.MovieEvent;
import com.example.commontest.dto.WatchEvent;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;

@Service
@Slf4j
@ConditionalOnProperty(prefix = "broker", name = "type", havingValue = "reactive-kafka")
public class ReactiveKafkaConsumer extends MessageConsumer {

    private final KafkaReceiver<String, MovieEvent> kafkaMovieEventReceiver;
    private final KafkaReceiver<String, WatchEvent> kafkaWatchEventReceiver;

    public ReactiveKafkaConsumer(UserPreferencesRepository preferencesRepository,
                                 KafkaReceiver<String, MovieEvent> kafkaMovieEventReceiver,
                                 KafkaReceiver<String, WatchEvent> kafkaWatchEventReceiver) {
        super(preferencesRepository);
        this.kafkaMovieEventReceiver = kafkaMovieEventReceiver;
        this.kafkaWatchEventReceiver = kafkaWatchEventReceiver;
    }

    @PostConstruct
    public void startConsumer() {
        consumeMovieEvents()
                .flatMap(event -> switch (event.getAction()) {
                    case ADD -> updatePreferencesForUsersWithNewMovie(event.getMovie());
                    case UPDATE -> updatePreferencesForUsersWithUpdatedMovie(event.getMovie());
                    case DELETE -> updatePreferencesForUsersWithDeletedMovie(event.getMovie());
                })
                .subscribe();

        consumeWatchEvents()
                .flatMap(this::updateUserPreferencesWhenWatchedMovie)
                .subscribe();
    }

    private Flux<MovieEvent> consumeMovieEvents() {
        return kafkaMovieEventReceiver.receive()
                .doOnNext(record -> log.info("Получено сообщение о событии: " + record.value()))
                .map(ConsumerRecord::value)
                .doOnError(e -> log.error("ОШИБКА: " + e.getMessage()));
    }

    private Flux<WatchEvent> consumeWatchEvents() {
        return kafkaWatchEventReceiver.receive()
                .doOnNext(record -> log.info("Получено сообщение о событии: " + record.value()))
                .map(ConsumerRecord::value)
                .doOnError(e -> log.error("ОШИБКА: " + e.getMessage()));
    }
}