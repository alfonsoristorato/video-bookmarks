package com.alfonsoristorato.common.kafka.service;

import com.alfonsoristorato.common.kafka.config.KafkaTopicConfigProperties;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.boot.actuate.health.Health;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
class KafkaServiceImplementation implements KafkaService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaTopicConfigProperties kafkaTopicConfigProperties;

    protected KafkaServiceImplementation(KafkaTemplate<String, String> kafkaTemplate, KafkaTopicConfigProperties kafkaTopicConfigProperties) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaTopicConfigProperties = kafkaTopicConfigProperties;
    }

    @Override
    public Mono<Health> getHealth() {
        ProducerRecord<String, String> kafkaMessage = new ProducerRecord<>(kafkaTopicConfigProperties.healthTopic(), "health","healthy?");
        try {
            this.sendMessage(kafkaMessage).get();
        } catch (ExecutionException | InterruptedException | KafkaException e) {
            return Mono.just(Health.down(e).build());
        }

        return Mono.just(Health.up().build());
    }

    @Override
    public CompletableFuture<SendResult<String,String>> sendMessage(ProducerRecord<String, String> kafkaMessage) {
        return kafkaTemplate.send(kafkaMessage);
    }
}
