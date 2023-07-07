package com.alfonsoristorato.bookmarkspersister.app.service;

import com.alfonsoristorato.common.cassandra.repository.Bookmark;
import com.alfonsoristorato.common.cassandra.repository.BookmarksRepository;
import com.alfonsoristorato.common.utils.models.BookmarkMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.ReceiverRecord;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PersisterServiceTest {

    @InjectMocks
    private PersisterService persisterService;

    @Mock
    private BookmarksRepository repository;

    @Mock
    private ReceiverRecord<String, String> receiverRecord;

    @Mock
    private ObjectMapper mapper;

    private final Instant now = Instant.parse("2023-06-07T10:15:30Z");

    private final BookmarkMessage bookmarkMessage = new BookmarkMessage("accountId", "userId", 1, 100, now);

    @Test
    void saveBookmark_callsRepositoryAndCompletesWhenRepositoryAndConverterSucceed() throws IOException {
        when(mapper.readValue(receiverRecord.value(), BookmarkMessage.class)).thenReturn(bookmarkMessage);
        when(repository.save(any(Bookmark.class))).thenReturn(Mono.empty());

        Mono<Void> response = persisterService.saveBookmark(receiverRecord);

        StepVerifier.create(response).expectSubscription().verifyComplete();
        verify(mapper).readValue(receiverRecord.value(), BookmarkMessage.class);
        verify(repository).save(any(Bookmark.class));
    }

    @Test
    void saveBookmark_callsRepositoryUpTo4TimesAndCompletesEvenIfRepositoryThrowsException() throws IOException {
        when(mapper.readValue(receiverRecord.value(), BookmarkMessage.class)).thenReturn(bookmarkMessage);
        when(repository.save(any(Bookmark.class))).thenReturn(Mono.error(new RuntimeException("any exception")));

        Mono<Void> response = persisterService.saveBookmark(receiverRecord);

        StepVerifier.create(response).expectSubscription().verifyComplete();
        verify(mapper).readValue(receiverRecord.value(), BookmarkMessage.class);
        verify(repository, times(4)).save(any(Bookmark.class));
    }

    @Test
    void saveBookmark_doesNotCallRepositoryButCompletesEvenIfConverterThrowsException() throws IOException {
        when(receiverRecord.value()).thenReturn("notCorrectFormatMessage");

        Mono<Void> response = persisterService.saveBookmark(receiverRecord);
        StepVerifier.create(response).expectSubscription().verifyComplete();

        verify(mapper).readValue(receiverRecord.value(), BookmarkMessage.class);
        verify(repository, never()).save(any(Bookmark.class));
    }
}
