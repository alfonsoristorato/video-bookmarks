package com.alfonsoristorato.bookmarkspersister.app.service;

import com.alfonsoristorato.common.cassandra.repository.Bookmark;
import com.alfonsoristorato.common.cassandra.repository.BookmarkPrimaryKey;
import com.alfonsoristorato.common.cassandra.repository.BookmarksRepository;
import com.alfonsoristorato.common.utils.models.BookmarkMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.ReceiverRecord;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Optional;

@Service
public class PersisterService {

    private final BookmarksRepository repository;

    private final ObjectMapper mapper;

    private final Logger log = LoggerFactory.getLogger(PersisterService.class);

    public PersisterService(BookmarksRepository repository, ObjectMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    Mono<Void> saveBookmark(ReceiverRecord<String, String> message) {
        return convertToBookmarkMessage(message)
                .map(bookmarkMessage -> {
                    BookmarkPrimaryKey bookmarkPrimaryKey = new BookmarkPrimaryKey(bookmarkMessage.accountId(), bookmarkMessage.userId(), String.valueOf(bookmarkMessage.videoId()));
                    return new Bookmark(bookmarkPrimaryKey, bookmarkMessage.bookmarkPosition(), bookmarkMessage.timestamp().toEpochMilli());
                })
                .map(bookmark ->
                        Mono.defer(() -> repository.save(bookmark)
                                        .doOnError(error -> log.error("Error writing to Cassandra, error: {}", error.getMessage())))
                                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2)))
                                .onErrorResume(error -> {
                                    log.error("Error writing to Cassandra after 4 attempts, error: {}", error.getMessage());
                                    return Mono.empty();
                                })
                                .then())
                .orElse(Mono.empty());
    }

    private Optional<BookmarkMessage> convertToBookmarkMessage(ReceiverRecord<String, String> message) {
        try {
            return Optional.ofNullable(mapper.readValue(message.value(), BookmarkMessage.class));
        } catch (JsonProcessingException e) {
            log.error("Error converting Kafka message to a valid Bookmark Message, error: {}", e.getMessage());
            return Optional.empty();
        }
    }

    //TODO: add a validator to compare the kafkamessage key (accountId) against the accountId in the kafkamessage value - check that it is a UUID too

}
