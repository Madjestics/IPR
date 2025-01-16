package com.example.brokerstest.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConditionalOnProperty(value = "broker.type", havingValue = "kafka")
@ConfigurationProperties(prefix = "kafka")
public class KafkaProperties {
    private String bootstrapServers;
    private String groupId;
    private String autoOffsetReset;
    private String moviesEventsTopic;
    private String watchEventsTopic;
}
