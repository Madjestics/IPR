package com.example.webfluxtest.service.impl;

import com.example.commontest.dto.MovieEvent;
import com.example.commontest.dto.WatchEvent;
import com.example.webfluxtest.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(prefix = "broker", name = "type", havingValue = "reactive-kafka")
public class MessageKafkaReactiveService implements MessageService {

    private final KafkaSender<String, Object> sender;

    @Value("${kafka.topic.movie-events:movies-events}")
    private String kafkaMovieEventsTopic;

    @Value("${kafka.topic.watch-events:watch-events}")
    private String kafkaWatchEventsTopic;

    public void sendMovieEvent(MovieEvent movieEvent) {
        sender.send(Mono.just(SenderRecord.create(new ProducerRecord<>(kafkaMovieEventsTopic, movieEvent), null)))
                .doOnError(e -> log.error("Ошибка отправки сообщения: " + e.getMessage()))
                .doOnNext(result -> log.info("Сообщено отправлено с помощью reactive-kafka: " + result))
                .subscribe();
    }

    @Override
    public void sendWatchEvent(WatchEvent WatchEvent) {
        sender.send(Mono.just(SenderRecord.create(new ProducerRecord<>(kafkaWatchEventsTopic, WatchEvent), null)))
                .doOnError(e -> log.error("Ошибка отправки сообщения: " + e.getMessage()))
                .doOnNext(result -> log.info("Сообщено отправлено с помощью reactive-kafka: " + result))
                .subscribe();
    }
}
