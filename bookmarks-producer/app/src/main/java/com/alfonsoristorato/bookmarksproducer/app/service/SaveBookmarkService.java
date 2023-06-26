package com.alfonsoristorato.bookmarksproducer.app.service;

import com.alfonsoristorato.bookmarksproducer.app.models.SaveBookmarkMessage;
import com.alfonsoristorato.common.kafka.config.KafkaTopicConfigProperties;
import com.alfonsoristorato.common.kafka.service.KafkaService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class SaveBookmarkService {
    private final KafkaService kafkaService;
    private final KafkaTopicConfigProperties kafkaTopicConfigProperties;
    private final ObjectMapper mapper;

    public SaveBookmarkService(KafkaService kafkaService, KafkaTopicConfigProperties kafkaTopicConfigProperties, ObjectMapper mapper) {
        this.kafkaService = kafkaService;
        this.kafkaTopicConfigProperties = kafkaTopicConfigProperties;
        this.mapper = mapper;
    }

    private SaveBookmarkMessage createMessage(String accountId, String userId, Integer videoId, Integer bookmarkPosition, Instant timestamp) {
        return new SaveBookmarkMessage(accountId, userId, videoId, bookmarkPosition, timestamp);
    }

    public void sendKafkaMessage(String accountId, String userId, Integer videoId, Integer bookmarkPosition) throws JsonProcessingException {
        String topic = kafkaTopicConfigProperties.bookmarkTopic();
        Instant now = Instant.now();
        SaveBookmarkMessage message = createMessage(accountId, userId, videoId, bookmarkPosition, now);
        ProducerRecord<String, String> kafkaMessage = new ProducerRecord<>(topic, accountId, mapper.writeValueAsString(message));
        kafkaService.sendMessage(kafkaMessage);
    }
}
