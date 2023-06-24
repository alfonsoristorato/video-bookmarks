package com.alfonsoristorato.bookmarksproducer.service.controllers;

import com.alfonsoristorato.bookmarksproducer.service.models.BookmarkBody;
import com.alfonsoristorato.bookmarksproducer.service.validation.SaveBookmarkValidation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import validation.HeaderValidation;

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
    private HeaderValidation headerValidation;

    @Test
    void saveBookmark_callsRequiredDependenciesInOrder() {
        String accountId = UUID.randomUUID().toString();
        String userId = UUID.randomUUID().toString();

        ResponseEntity<Void> response = saveBookmarkController.saveBookmark("1", new BookmarkBody(1), accountId, userId);

        InOrder inOrder = inOrder(saveBookmarkValidation, headerValidation);
        inOrder.verify(headerValidation).validateHeaders(accountId, userId);
        inOrder.verify(saveBookmarkValidation).validateRequest("1", new BookmarkBody(1));
        assertThat(response).isEqualTo(ResponseEntity.accepted().build());

    }

}
