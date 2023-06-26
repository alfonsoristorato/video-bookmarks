package com.alfonsoristorato.common.kafka.health;

import com.alfonsoristorato.common.kafka.service.KafkaService;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component("KafkaHealthIndicator")
public class KafkaHealthIndicator implements ReactiveHealthIndicator {
    private final KafkaService kafkaService;

    public KafkaHealthIndicator(KafkaService kafkaService) {
        this.kafkaService = kafkaService;
    }

    @Override
    public Mono<Health> health() {
        return kafkaService.getHealth();
    }
}
