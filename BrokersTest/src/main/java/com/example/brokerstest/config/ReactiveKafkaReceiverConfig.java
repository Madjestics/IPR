package com.example.brokerstest.config;

import com.example.commontest.dto.MovieEvent;
import com.example.commontest.dto.WatchEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "broker", name = "type", havingValue = "reactive-kafka")
@EnableConfigurationProperties({KafkaProperties.class})
public class ReactiveKafkaReceiverConfig {
    private final KafkaProperties kafkaProperties;

    private Map<String, Object> receiverOptions() {
        return Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers(),
                ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getGroupId(),
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaProperties.getAutoOffsetReset()
        );
    }

    @Bean
    public KafkaReceiver<String, MovieEvent> kafkaMovieEventReceiver() {
        ReceiverOptions<String, MovieEvent> options = ReceiverOptions.<String, MovieEvent>create(receiverOptions())
                .withKeyDeserializer(new StringDeserializer())
                .withValueDeserializer(new JsonDeserializer<>(MovieEvent.class).trustedPackages("*"))
                .subscription(List.of(kafkaProperties.getMoviesEventsTopic()));

        return KafkaReceiver.create(options);
    }

    @Bean
    public KafkaReceiver<String, WatchEvent> kafkaWatchEventReceiver() {
        ReceiverOptions<String, WatchEvent> options = ReceiverOptions.<String, WatchEvent>create(receiverOptions())
                .withKeyDeserializer(new StringDeserializer())
                .withValueDeserializer(new JsonDeserializer<>(WatchEvent.class).trustedPackages("*"))
                .subscription(List.of(kafkaProperties.getWatchEventsTopic()));

        return KafkaReceiver.create(options);
    }
}
