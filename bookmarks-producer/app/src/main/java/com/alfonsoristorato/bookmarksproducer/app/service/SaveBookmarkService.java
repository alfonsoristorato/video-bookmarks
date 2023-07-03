package com.alfonsoristorato.bookmarksproducer.app.service;

import com.alfonsoristorato.bookmarksproducer.app.models.SaveBookmarkMessage;
import com.alfonsoristorato.common.kafka.config.KafkaTopicConfigProperties;
import com.alfonsoristorato.common.kafka.service.KafkaService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class SaveBookmarkService {
    private final KafkaService kafkaService;
    private final KafkaTopicConfigProperties kafkaTopicConfigProperties;
    private final ObjectMapper mapper;
    private final Logger logger = LoggerFactory.getLogger(SaveBookmarkService.class);

    public SaveBookmarkService(KafkaService kafkaService, KafkaTopicConfigProperties kafkaTopicConfigProperties, ObjectMapper mapper) {
        this.kafkaService = kafkaService;
        this.kafkaTopicConfigProperties = kafkaTopicConfigProperties;
        this.mapper = mapper;
    }

    private SaveBookmarkMessage createMessage(String accountId, String userId, Integer videoId, Integer bookmarkPosition, Instant timestamp) {
        return new SaveBookmarkMessage(accountId, userId, videoId, bookmarkPosition, timestamp);
    }

    private String createJsonMessage(String accountId, String userId, Integer videoId, Integer bookmarkPosition, Instant now) {
        try {
            return mapper.writeValueAsString(createMessage(accountId, userId, videoId, bookmarkPosition, now));
        }
        // should not happen
        catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendKafkaMessage(String accountId, String userId, Integer videoId, Integer bookmarkPosition) {
        String topic = kafkaTopicConfigProperties.bookmarkTopic();
        Instant now = Instant.now();
        String message = createJsonMessage(accountId, userId, videoId, bookmarkPosition, now);
        ProducerRecord<String, String> kafkaMessage = new ProducerRecord<>(topic, accountId, message);
        kafkaService.sendMessage(kafkaMessage)
                .doOnError((error) -> logger.error("Kafka message sending failed, exception: ", error))
                .subscribe();
    }
}
