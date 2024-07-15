package com.aston.kafka.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.Map;

@Slf4j
@ConfigurationProperties("app.common.kafka")
public record KafkaStarterProperties(
        @DefaultValue("true") boolean enabled,
        @DefaultValue("http://localhost:9092") String bootstrapServers,
        @DefaultValue TopicProperties topic,
        @DefaultValue ProducerProperties producer,
        @DefaultValue ConsumerProperties consumer
) {

    public record TopicProperties(
            @DefaultValue("notification") String userLogin,
            @DefaultValue("requisites") String updateTaxNum) {
    }

    public record ProducerProperties(
            @DefaultValue("1") String acks,
            @DefaultValue("org.apache.kafka.common.serialization.StringSerializer")
            String keySerializer,
            @DefaultValue("org.springframework.kafka.support.serializer.JsonSerializer")
            String valueSerializer,
            @DefaultValue("false") boolean addHeaderOption
    ) {
    }

    public record ConsumerProperties(
            Map<String, String> groupId,
            @DefaultValue("earliest") String autoOffsetReset
    ) {
    }

    @PostConstruct
    void init() {
        log.info("KafkaStarterProperties initialized: %n{}", this);
    }
}
