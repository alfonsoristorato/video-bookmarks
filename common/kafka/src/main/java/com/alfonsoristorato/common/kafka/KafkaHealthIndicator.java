package com.alfonsoristorato.common.kafka;

import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${downstreams.kafka.topics.health}")
    @NotBlank private String healthTopic;

    public KafkaHealthIndicator(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public Mono<Health> health() {
        try {
            kafkaTemplate.send(healthTopic, "healthy?").get();
        } catch (ExecutionException | InterruptedException | KafkaException e) {
            return Mono.just(Health.down(e).build());
        }

        return Mono.just(Health.up().build());
    }
}
