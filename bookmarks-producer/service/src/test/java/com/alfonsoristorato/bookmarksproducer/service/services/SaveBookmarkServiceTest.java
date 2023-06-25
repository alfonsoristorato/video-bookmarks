package com.alfonsoristorato.bookmarksproducer.service.services;

import com.alfonsoristorato.bookmarksproducer.service.config.KafkaTopicConfigProperties;
import com.alfonsoristorato.bookmarksproducer.service.models.SaveBookmarkMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SaveBookmarkServiceTest {

    @InjectMocks
    private SaveBookmarkService saveBookmarkService;

    @Mock
    private KafkaTemplate<String,String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private KafkaTopicConfigProperties kafkaTopicConfigProperties;


    @Test
    void sendKafkaMessage_callsRequiredDependenciesInOrder() throws JsonProcessingException {
        String accountId = UUID.randomUUID().toString();
        String userId = UUID.randomUUID().toString();
        Integer videoId = 1;
        Integer bookmarkPosition = 100;
        Clock fixedClock = Clock.fixed(Instant.parse("2023-12-22T10:15:30.100Z"), Clock.systemUTC().getZone());
        Instant instant = Instant.now(fixedClock);

        try (MockedStatic<Instant> mockedStatic = mockStatic(Instant.class)) {
            mockedStatic.when(Instant::now).thenReturn(instant);
            Instant now = Instant.now();
            String messageJson =
                    String.format("{\"accountId\":\"%s\",\"userId\":\"%s\",\"videoId\":1,\"bookmarkPosition\":100,\"timestamp\":\"%s\"}",
                            accountId, userId, now);
            ProducerRecord<String,String> kafkaMessage = new ProducerRecord<>("topic",accountId,messageJson);

            when(kafkaTopicConfigProperties.bookmarkTopic()).thenReturn("topic");
            when(objectMapper.writeValueAsString(any(SaveBookmarkMessage.class))).thenReturn(messageJson);

            saveBookmarkService.sendKafkaMessage(accountId,userId,videoId,bookmarkPosition);

            InOrder inOrder = inOrder(kafkaTopicConfigProperties,objectMapper,kafkaTemplate);

            inOrder.verify(kafkaTopicConfigProperties).bookmarkTopic();
            inOrder.verify(objectMapper).writeValueAsString(new SaveBookmarkMessage(accountId,userId,videoId,bookmarkPosition,now));
            inOrder.verify(kafkaTemplate).send(kafkaMessage);
        }
    }

}
