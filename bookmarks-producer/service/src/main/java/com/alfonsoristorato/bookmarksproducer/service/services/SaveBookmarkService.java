package com.alfonsoristorato.bookmarksproducer.service.services;

import com.alfonsoristorato.bookmarksproducer.service.config.KafkaTopicConfigProperties;
import com.alfonsoristorato.bookmarksproducer.service.models.SaveBookmarkMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class SaveBookmarkService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaTopicConfigProperties kafkaTopicConfigProperties;
    private final ObjectMapper mapper;

    public SaveBookmarkService(KafkaTemplate<String, String> kafkaTemplate, KafkaTopicConfigProperties kafkaTopicConfigProperties, ObjectMapper mapper) {
        this.kafkaTemplate = kafkaTemplate;
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
        kafkaTemplate.send(kafkaMessage);
    }
}
