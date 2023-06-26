package com.alfonsoristorato.common.kafka.service;

import com.alfonsoristorato.common.kafka.config.KafkaTopicConfigProperties;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import reactor.test.StepVerifier;

import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class KafkaServiceImplementationTest {
    @InjectMocks
    private KafkaServiceImplementation kafkaServiceImplementation;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private KafkaTopicConfigProperties kafkaTopicConfigProperties;

    @BeforeEach
    void setUp() {
        when(kafkaTopicConfigProperties.healthTopic()).thenReturn("health-indicator");
    }

    @Test
    void getHealth_shouldReturnUpWhenTheServiceIsUp() {
        CompletableFuture<SendResult<String, String>> future =
                CompletableFuture.completedFuture(new SendResult<>(new ProducerRecord<>("kafka-health-indicator", "", ""), null));
        when(kafkaTemplate.send("health-indicator", "healthy?")).thenReturn(future);

        StepVerifier.create(kafkaServiceImplementation.getHealth())
                .expectNextMatches(health -> health.getStatus().equals(Health.up().build().getStatus()))
                .verifyComplete();
    }

    @Test
    void getHealth_shouldReturnDownWhenTheServiceThrowsException() {
        CompletableFuture<SendResult<String, String>> future =
                CompletableFuture.supplyAsync(() -> {
                    throw new RuntimeException("exception");
                });
        when(kafkaTemplate.send("health-indicator", "healthy?")).thenReturn(future);

        StepVerifier.create(kafkaServiceImplementation.getHealth())
                .expectNextMatches(health -> health.getStatus().equals(Health.down().build().getStatus()))
                .verifyComplete();
    }
}
