package com.aston.kafka.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@EnableConfigurationProperties(KafkaStarterProperties.class)
@ConditionalOnClass(KafkaStarterProperties.class)
@ConditionalOnProperty(
        prefix = "app.common.kafka",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true)
@RequiredArgsConstructor
public class KafkaAutoConfiguration {

    private final KafkaStarterProperties properties;

    @PostConstruct
    void init() {
        log.info("KafkaAutoConfiguration initialized");
    }

    @Bean
    public KafkaAdmin admin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, properties.bootstrapServers());
        return new KafkaAdmin(configs);
    }

    @Bean
    @ConditionalOnMissingBean
    public NewTopic userLoginTopic() {
        return TopicBuilder
                .name(properties.topic().userLogin())
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    public NewTopic updateTaxNumTopic() {
        return TopicBuilder
                .name(properties.topic().updateTaxNum())
                .build();
    }

    @Bean
    @Primary
    public RecordMessageConverter recordMessageConverter() {
        return new StringJsonMessageConverter(new ObjectMapper());
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean
    public DefaultKafkaProducerFactory<?, ?> kafkaProducerFactory(KafkaProperties kafkaProperties) {
        var producerProperties = kafkaProperties.buildProducerProperties(null);
        producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.bootstrapServers());
        producerProperties.put(ProducerConfig.ACKS_CONFIG, properties.producer().acks());
        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        producerProperties.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, "false");
        return new DefaultKafkaProducerFactory<>(producerProperties);
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean
    public DefaultKafkaConsumerFactory<?, ?> kafkaConsumerFactory(KafkaProperties kafkaProperties) {
        var consumerProperties = kafkaProperties.buildConsumerProperties(null);
        consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.bootstrapServers());
        consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, "default-kafka-group");
        consumerProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, properties.consumer().autoOffsetReset());
        return new DefaultKafkaConsumerFactory<>(consumerProperties);
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean
    public KafkaListenerContainerFactory<?> kafkaListenerContainerFactory(DefaultKafkaConsumerFactory kafkaConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<Integer, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(kafkaConsumerFactory);
        factory.setRecordMessageConverter(recordMessageConverter());
        return factory;
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean
    public KafkaTemplate<?, ?> kafkaTemplate(DefaultKafkaProducerFactory kafkaProducerFactory) {
        KafkaTemplate<?, ?> kafkaTemplate = new KafkaTemplate<>(kafkaProducerFactory);
        kafkaTemplate.setMessageConverter(recordMessageConverter());
        return kafkaTemplate;
    }
}
