package com.example.brokerstest.service.impl;

import com.example.brokerstest.repository.UserPreferencesRepository;
import com.example.brokerstest.service.MessageConsumer;
import com.example.commontest.dto.MovieEvent;
import com.example.commontest.dto.WatchEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@ConditionalOnProperty(prefix = "broker", name = "type", havingValue = "rabbit")
public class RabbitConsumer extends MessageConsumer {
    public RabbitConsumer(UserPreferencesRepository preferencesRepository) {
        super(preferencesRepository);
    }

    @RabbitListener(queues = "${rabbitmq.queue.movie}")
    public void handleMovieEventFromRabbitMQ(MovieEvent event) {
        log.info("Получено сообщение от rabbit:" + event);
        handleMovieEvent(event);
    }

    @RabbitListener(queues = "${rabbitmq.queue.watch}")
    public void handleWatchEventFromRabbitMQ(WatchEvent event) {
        log.info("Получено сообщение от rabbit:" + event);
        handleWatchEvent(event);
    }
}
