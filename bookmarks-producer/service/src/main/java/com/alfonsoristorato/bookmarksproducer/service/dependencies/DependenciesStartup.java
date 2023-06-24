package com.alfonsoristorato.bookmarksproducer.service.dependencies;

import com.alfonsoristorato.common.kafka.KafkaHealthIndicator;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import java.util.Objects;


@Import(KafkaHealthIndicator.class)
@Component
public class DependenciesStartup {
    private final KafkaHealthIndicator kafkaHealthIndicator;

    public DependenciesStartup(KafkaHealthIndicator kafkaHealthIndicator) {
        this.kafkaHealthIndicator = kafkaHealthIndicator;
    }

    @PostConstruct
    public void onStartup() {
        Health kafkaHealth = kafkaHealthIndicator.health().block();
        if (Objects.requireNonNull(kafkaHealth).getStatus().equals(Status.DOWN)) {
            throw new RuntimeException("Kafka is down.");
        }
    }
}
