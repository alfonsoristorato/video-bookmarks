package com.alfonsoristorato.bookmarksproducer.controllers;

import com.alfonsoristorato.bookmarksproducer.models.BookmarkBody;
import com.alfonsoristorato.bookmarksproducer.validation.SaveBookmarkValidation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;

@ExtendWith(MockitoExtension.class)
public class SaveBookmarkControllerTest {
    @InjectMocks
    private SaveBookmarkController saveBookmarkController;

    @Mock
    private SaveBookmarkValidation saveBookmarkValidation;

    @Test
    void saveBookmark_callsRequiredDependenciesInOrder() {
        ResponseEntity<Void> response = saveBookmarkController.saveBookmark("1", new BookmarkBody(1));
        InOrder inOrder = inOrder(saveBookmarkValidation);
        inOrder.verify(saveBookmarkValidation).validateRequest("1", new BookmarkBody(1));
        assertThat(response).isEqualTo(ResponseEntity.accepted().build());

    }

}
