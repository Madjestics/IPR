package com.example.brokerstest.service.impl;

import com.example.brokerstest.repository.UserPreferencesRepository;
import com.example.brokerstest.service.MessageConsumer;
import com.example.commontest.dto.MovieEvent;
import com.example.commontest.dto.WatchEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@ConditionalOnProperty(prefix = "broker", name = "type", havingValue = "kafka")
public class KafkaConsumer extends MessageConsumer {

    public KafkaConsumer(UserPreferencesRepository preferencesRepository) {
        super(preferencesRepository);
    }

    @KafkaListener(topics = "${kafka.topic.movies-events}", groupId = "${kafka.group-id}")
    public void handleMovieEventFromKafka(MovieEvent event) {
        log.info("Получено сообщение от kafka:" + event);
        handleMovieEvent(event);
    }

    @KafkaListener(topics = "${kafka.topic.watch-events}", groupId = "${kafka.group-id}")
    public void handleWatchEventFromKafka(WatchEvent event) {
        log.info("Получено сообщение от kafka:" + event);
        handleWatchEvent(event);
    }
}
