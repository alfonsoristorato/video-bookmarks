package com.alfonsoristorato.common.kafka.service;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.boot.actuate.health.Health;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.ReceiverRecord;
import reactor.kafka.sender.SenderResult;

public interface KafkaService {
    Mono<Health> getHealth();

    Mono<SenderResult<Void>> sendMessage(ProducerRecord<String, String> kafkaMessage);

    Flux<ReceiverRecord<String,String>> consumeMessages();
}
