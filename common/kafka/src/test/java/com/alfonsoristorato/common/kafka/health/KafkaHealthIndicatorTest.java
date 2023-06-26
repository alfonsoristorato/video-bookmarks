package com.alfonsoristorato.common.kafka.health;

import com.alfonsoristorato.common.kafka.service.KafkaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class KafkaHealthIndicatorTest {
    @InjectMocks
    private KafkaHealthIndicator kafkaHealthIndicator;

    @Mock
    private KafkaService kafkaService;

    @Test
    void health_shouldReturnUpWhenTheServiceReturnsUp() {
        when(kafkaService.getHealth()).thenReturn(Mono.just(Health.up().build()));

        StepVerifier.create(kafkaHealthIndicator.health())
                .expectNextMatches(health -> health.getStatus().equals(Health.up().build().getStatus()))
                .verifyComplete();
    }

    @Test
    void health_shouldReturnDownWhenTheServiceReturnsDown() {
        when(kafkaService.getHealth()).thenReturn(Mono.just(Health.down().build()));

        StepVerifier.create(kafkaHealthIndicator.health())
                .expectNextMatches(health -> health.getStatus().equals(Health.down().build().getStatus()))
                .verifyComplete();
    }
}
