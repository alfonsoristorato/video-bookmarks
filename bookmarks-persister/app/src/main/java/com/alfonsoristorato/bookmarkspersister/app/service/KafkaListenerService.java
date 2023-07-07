package com.alfonsoristorato.bookmarkspersister.app.service;

import com.alfonsoristorato.common.kafka.service.KafkaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaListenerService {

    private final KafkaService kafkaService;
    private final PersisterService persisterService;

    private final Logger log = LoggerFactory.getLogger(KafkaListenerService.class);


    public KafkaListenerService(KafkaService kafkaService, PersisterService persisterService) {
        this.kafkaService = kafkaService;
        this.persisterService = persisterService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void listen() {
        kafkaService
                .consumeMessages()
                .doOnError(error -> log.error("Error consuming messages, error: {}", error.getMessage()))
                .delayUntil(persisterService::saveBookmark)
                .doOnNext(message -> message.receiverOffset().acknowledge())
                .subscribe();
    }
}
