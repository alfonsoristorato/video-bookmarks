package com.alfonsoristorato.bookmarksproducer.app.controllers;

import com.alfonsoristorato.bookmarksproducer.app.models.BookmarkBody;
import com.alfonsoristorato.bookmarksproducer.app.service.SaveBookmarkService;
import com.alfonsoristorato.bookmarksproducer.app.validation.SaveBookmarkValidation;
import com.alfonsoristorato.common.utils.validation.HeaderValidation;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;

@ExtendWith(MockitoExtension.class)
public class SaveBookmarkControllerTest {
    @InjectMocks
    private SaveBookmarkController saveBookmarkController;

    @Mock
    private SaveBookmarkValidation saveBookmarkValidation;

    @Mock
    private SaveBookmarkService saveBookmarkService;

    @Mock
    private HeaderValidation headerValidation;

    @Test
    void saveBookmark_callsRequiredDependenciesInOrder() throws JsonProcessingException {
        String accountId = UUID.randomUUID().toString();
        String userId = UUID.randomUUID().toString();

        ResponseEntity<Void> response = saveBookmarkController.saveBookmark("1", new BookmarkBody(100), accountId, userId);

        InOrder inOrder = inOrder(saveBookmarkValidation, headerValidation, saveBookmarkService);
        inOrder.verify(headerValidation).validateHeaders(accountId, userId);
        inOrder.verify(saveBookmarkValidation).validateRequest("1", new BookmarkBody(100));
        inOrder.verify(saveBookmarkService).sendKafkaMessage(accountId, userId, 1, 100);
        assertThat(response).isEqualTo(ResponseEntity.accepted().build());

    }

}
