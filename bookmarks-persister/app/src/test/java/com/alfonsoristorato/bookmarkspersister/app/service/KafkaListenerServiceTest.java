package com.alfonsoristorato.bookmarkspersister.app.service;

import com.alfonsoristorato.common.kafka.service.KafkaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.ReceiverRecord;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KafkaListenerServiceTest {

    @InjectMocks
    private KafkaListenerService kafkaListenerService;

    @Mock
    private PersisterService persisterService;

    @Mock
    private KafkaService kafkaService;

    @Mock
    private ReceiverRecord<String, String> receiverRecord;


    @Test
    void listen_callsPersisterServiceAndCompletesEvenIfMessagePassedToPersisterServiceIsInvalid() {
        when(kafkaService.consumeMessages()).thenReturn(Flux.just(receiverRecord));

        kafkaListenerService.listen();

        verify(kafkaService).consumeMessages();
        verify(persisterService).saveBookmark(receiverRecord);
    }

    @Test
    void listen_stopsExecutionIfConsumeMessageFails() {
        when(kafkaService.consumeMessages()).thenReturn(Flux.error(new RuntimeException("consume error")));

        kafkaListenerService.listen();

        verify(kafkaService).consumeMessages();
        verify(persisterService, never()).saveBookmark(receiverRecord);
    }
}
