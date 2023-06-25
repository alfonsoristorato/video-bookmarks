package com.alfonsoristorato.common.kafka;

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

import java.lang.reflect.Field;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class KafkaHealthIndicatorTest {
    @InjectMocks
    private KafkaHealthIndicator kafkaHealthIndicator;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        Field healthTopicField = KafkaHealthIndicator.class.getDeclaredField("healthTopic");
        healthTopicField.setAccessible(true);
        healthTopicField.set(kafkaHealthIndicator, "health-indicator");
    }

    @Test
    void health_shouldReturnUpWhenTheServiceIsUp() {
        CompletableFuture<SendResult<String, String>> future =
                CompletableFuture.completedFuture(new SendResult<>(new ProducerRecord<>("kafka-health-indicator", "", ""), null));
        when(kafkaTemplate.send("health-indicator", "healthy?")).thenReturn(future);

        StepVerifier.create(kafkaHealthIndicator.health())
                .expectNextMatches(health -> health.getStatus().equals(Health.up().build().getStatus()))
                .verifyComplete();
    }

    @Test
    void health_shouldReturnDownWhenTheServiceThrowsException() {
        CompletableFuture<SendResult<String, String>> future =
                CompletableFuture.supplyAsync(() -> {
                    throw new RuntimeException("exception");
                });
        when(kafkaTemplate.send("health-indicator", "healthy?")).thenReturn(future);

        StepVerifier.create(kafkaHealthIndicator.health())
                .expectNextMatches(health -> health.getStatus().equals(Health.down().build().getStatus()))
                .verifyComplete();
    }
}
