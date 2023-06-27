package com.alfonsoristorato.bookmarksproducer.app.controllers;

import com.alfonsoristorato.bookmarksproducer.app.models.BookmarkBody;
import com.alfonsoristorato.bookmarksproducer.app.service.SaveBookmarkService;
import com.alfonsoristorato.bookmarksproducer.app.validation.SaveBookmarkValidation;
import com.alfonsoristorato.common.signatureverifier.config.SignatureVerifierConfigProperties;
import com.alfonsoristorato.common.signatureverifier.service.SignatureVerifierService;
import com.alfonsoristorato.common.utils.validation.HeaderValidation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

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

    @Mock
    private SignatureVerifierService signatureVerifierService;

    @Mock
    private SignatureVerifierConfigProperties signatureVerifierConfigProperties;

    @Test
    void saveBookmark_callsRequiredDependenciesInOrder() {
        String accountId = UUID.randomUUID().toString();
        String userId = UUID.randomUUID().toString();
        String signature = "signature";
        when(signatureVerifierConfigProperties.verifyPath()).thenReturn("verify");
        when(signatureVerifierService.verifySignature(signatureVerifierConfigProperties.verifyPath(), signature)).thenReturn(Mono.just(ResponseEntity.noContent().build()));
        Mono<ResponseEntity<Void>> response = saveBookmarkController.saveBookmark("1", new BookmarkBody(100), accountId, userId, signature);
        response.block();

        InOrder inOrder = inOrder(
                saveBookmarkValidation,
                headerValidation,
                saveBookmarkService,
                signatureVerifierService);

        inOrder.verify(headerValidation).validateHeaders(accountId, userId, signature);
        inOrder.verify(saveBookmarkValidation).validateRequest("1", new BookmarkBody(100));
        inOrder.verify(signatureVerifierService).verifySignature(signatureVerifierConfigProperties.verifyPath(), signature);
        inOrder.verify(saveBookmarkService).sendKafkaMessage(accountId, userId, 1, 100);
        StepVerifier.create(response)
                .expectNext(ResponseEntity.accepted().build())
                .verifyComplete();

    }

}
