package com.alfonsoristorato.common.kafka.service;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.boot.actuate.health.Health;
import org.springframework.kafka.support.SendResult;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

public interface KafkaService {
    Mono<Health> getHealth();

    CompletableFuture<SendResult<String, String>> sendMessage(ProducerRecord<String, String> kafkaMessage);
}
