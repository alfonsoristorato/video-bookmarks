package com.alfonsoristorato.bookmarksproducer.app.service;

import com.alfonsoristorato.common.kafka.config.KafkaTopicConfigProperties;
import com.alfonsoristorato.common.kafka.service.KafkaService;
import com.alfonsoristorato.common.utils.models.BookmarkMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.SenderResult;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProducerServiceTest {

    @InjectMocks
    private ProducerService producerService;

    @Mock
    private KafkaService kafkaService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private SenderResult<Void> senderResult;

    @Mock
    private KafkaTopicConfigProperties kafkaTopicConfigProperties;

    @Mock
    private Clock clock;


    @Test
    void sendKafkaMessage_callsRequiredDependenciesInOrder() throws JsonProcessingException {
        String accountId = UUID.randomUUID().toString();
        String userId = UUID.randomUUID().toString();
        Integer videoId = 1;
        Integer bookmarkPosition = 100;
        when(clock.instant()).thenReturn(Instant.parse("2023-12-22T10:15:30.100Z"));
        Instant now = clock.instant();
        String messageJson =
                String.format("{\"accountId\":\"%s\",\"userId\":\"%s\",\"videoId\":1,\"bookmarkPosition\":100,\"timestamp\":\"%s\"}",
                        accountId, userId, now);
        ProducerRecord<String, String> kafkaMessage = new ProducerRecord<>("topic", accountId, messageJson);
        when(kafkaTopicConfigProperties.bookmarkTopic()).thenReturn("topic");
        when(objectMapper.writeValueAsString(any(BookmarkMessage.class))).thenReturn(messageJson);
        when(kafkaService.sendMessage(any())).thenReturn(Mono.just(senderResult));

        producerService.sendKafkaMessage(accountId, userId, videoId, bookmarkPosition);

        InOrder inOrder = inOrder(kafkaTopicConfigProperties,clock, objectMapper, kafkaService);

        inOrder.verify(kafkaTopicConfigProperties).bookmarkTopic();
        inOrder.verify(clock).instant();
        inOrder.verify(objectMapper).writeValueAsString(new BookmarkMessage(accountId, userId, videoId, bookmarkPosition, now));
        inOrder.verify(kafkaService).sendMessage(kafkaMessage);
    }


}
