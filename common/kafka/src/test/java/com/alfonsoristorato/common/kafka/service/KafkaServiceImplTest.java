package com.alfonsoristorato.common.kafka.service;

import com.alfonsoristorato.common.kafka.config.KafkaTopicConfigProperties;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.SenderResult;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class KafkaServiceImplTest {
    @InjectMocks
    private KafkaServiceImpl kafkaServiceImpl;

    @Mock
    private ReactiveKafkaProducerTemplate<String, String> reactiveKafkaProducerTemplate;

    @Mock
    private ReactiveKafkaConsumerTemplate<String, String> reactiveKafkaConsumerTemplate;

    @Mock
    private KafkaTopicConfigProperties kafkaTopicConfigProperties;

    @Mock
    private SenderResult<Void> senderResult;

    @Test
    void getHealth_shouldReturnUpWhenTheServiceIsUp() {
        when(kafkaTopicConfigProperties.healthTopic()).thenReturn("health-indicator");
        ProducerRecord<String, String> kafkaMessage = new ProducerRecord<>("health-indicator", "health", "healthy?");

        when(reactiveKafkaProducerTemplate.send(kafkaMessage)).thenReturn(Mono.just(senderResult));

        StepVerifier.create(kafkaServiceImpl.getHealth())
                .expectNextMatches(health -> health.getStatus().equals(Health.up().build().getStatus()))
                .verifyComplete();
    }

    @Test
    void getHealth_shouldReturnDownWhenTheServiceThrowsException() {
        when(kafkaTopicConfigProperties.healthTopic()).thenReturn("health-indicator");
        ProducerRecord<String, String> kafkaMessage = new ProducerRecord<>("health-indicator", "health", "healthy?");

        when(reactiveKafkaProducerTemplate.send(kafkaMessage)).thenReturn(Mono.error(new RuntimeException()));

        StepVerifier.create(kafkaServiceImpl.getHealth())
                .expectNextMatches(health -> health.getStatus().equals(Health.down().build().getStatus()))
                .verifyComplete();
    }

    @Test
    void sendMessage_callsReactiveKafkaProducerTemplate() {
        ProducerRecord<String, String> kafkaMessage = new ProducerRecord<>("health-indicator", "health", "healthy?");

        kafkaServiceImpl.sendMessage(kafkaMessage);

        verify(reactiveKafkaProducerTemplate).send(kafkaMessage);

    }

    @Test
    void consumeMessages_callsReactiveKafkaConsumerTemplate() {
        kafkaServiceImpl.consumeMessages();

        verify(reactiveKafkaConsumerTemplate).receive();

    }
}
