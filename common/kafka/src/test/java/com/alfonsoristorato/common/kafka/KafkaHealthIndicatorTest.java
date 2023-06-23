package com.alfonsoristorato.common.kafka;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class KafkaHealthIndicatorTest {
    @InjectMocks
    private KafkaHealthIndicator kafkaHealthIndicator;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Test
    void healthShouldReturnUpWhenTheServiceIsUp() {
        CompletableFuture<SendResult<String, String>> future =
                CompletableFuture.completedFuture(new SendResult<>(new ProducerRecord<>("kafka-health-indicator", "", ""), null));
        when(kafkaTemplate.send("kafka-health-indicator", "health")).thenReturn(future);

        Mono<Health> kafkaHealth = kafkaHealthIndicator.health();
        assertThat(Objects.requireNonNull(kafkaHealth.block()).getStatus()).isEqualTo(Status.UP);
    }

    @Test
    void healthShouldReturnDownWhenTheServiceThrowsException() {
        CompletableFuture<SendResult<String, String>> future =
                CompletableFuture.supplyAsync(() -> {
                    throw new RuntimeException("exception");
                });
        when(kafkaTemplate.send("kafka-health-indicator", "health")).thenReturn(future);

        Mono<Health> kafkaHealth = kafkaHealthIndicator.health();
        assertThat(Objects.requireNonNull(kafkaHealth.block()).getStatus()).isEqualTo(Status.DOWN);
    }
}
