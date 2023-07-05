package com.alfonsoristorato.common.kafka.service;

import com.alfonsoristorato.common.kafka.config.KafkaTopicConfigProperties;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.boot.actuate.health.Health;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.ReceiverRecord;
import reactor.kafka.sender.SenderResult;

@Service
class KafkaServiceImpl implements KafkaService {

    private final KafkaTopicConfigProperties kafkaTopicConfigProperties;

    private final ReactiveKafkaProducerTemplate<String, String> reactiveKafkaProducerTemplate;

    private final ReactiveKafkaConsumerTemplate<String, String> reactiveKafkaConsumerTemplate;


    protected KafkaServiceImpl(KafkaTopicConfigProperties kafkaTopicConfigProperties, ReactiveKafkaProducerTemplate<String, String> reactiveKafkaProducerTemplate, ReactiveKafkaConsumerTemplate<String, String> reactiveKafkaConsumerTemplate) {
        this.reactiveKafkaProducerTemplate = reactiveKafkaProducerTemplate;
        this.kafkaTopicConfigProperties = kafkaTopicConfigProperties;
        this.reactiveKafkaConsumerTemplate = reactiveKafkaConsumerTemplate;
    }

    @Override
    public Mono<Health> getHealth() {
        ProducerRecord<String, String> kafkaMessage = new ProducerRecord<>(kafkaTopicConfigProperties.healthTopic(), "health", "healthy?");
        return this.sendMessage(kafkaMessage)
                .map(success -> Health.up().build())
                .onErrorReturn(Health.down().build());
    }

    @Override
    public Mono<SenderResult<Void>> sendMessage(ProducerRecord<String, String> kafkaMessage) {
        return reactiveKafkaProducerTemplate.send(kafkaMessage);
    }
    @Override
    public Flux<ReceiverRecord<String,String>> consumeMessages(){
        return reactiveKafkaConsumerTemplate.receive();
    }
}
