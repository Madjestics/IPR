package com.example.webfluxtest.service.impl;

import com.example.commontest.dto.MovieEvent;
import com.example.commontest.dto.WatchEvent;
import com.example.webfluxtest.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "broker", name = "type", havingValue = "kafka")
public class KakfaMessageService implements MessageService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.movie-events:movies-add-events}")
    private String kafkaMovieEventsTopic;

    @Value("${kafka.topic.watch-events:watch-events}")
    private String kafkaWatchEventsTopic;

    @Override
    public void sendMovieEvent(MovieEvent movieEvent) {
        kafkaTemplate.send(kafkaMovieEventsTopic, movieEvent);
        log.info("В Kafka отправлено сообщение о фильме: " + movieEvent);
    }

    @Override
    public void sendWatchEvent(WatchEvent watchEvent) {
        kafkaTemplate.send(kafkaWatchEventsTopic, watchEvent);
        log.info(String.format("В Kafka отправлено сообщение о просмотре фильма %s пользователем %s", watchEvent.getMovie(), watchEvent.getUserId()));
    }
}
