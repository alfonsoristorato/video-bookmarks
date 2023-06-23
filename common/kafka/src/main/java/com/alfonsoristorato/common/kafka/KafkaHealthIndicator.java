package com.alfonsoristorato.common.kafka;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.concurrent.ExecutionException;

@Component("KafkaHealthIndicator")
public class KafkaHealthIndicator implements ReactiveHealthIndicator {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaHealthIndicator(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public Mono<Health> health() {
        try {
            kafkaTemplate.send("kafka-health-indicator", "health").get();
        } catch (ExecutionException | InterruptedException | KafkaException e) {
            return Mono.just(Health.down(e).build());
        }

        return Mono.just(Health.up().build());
    }
}
