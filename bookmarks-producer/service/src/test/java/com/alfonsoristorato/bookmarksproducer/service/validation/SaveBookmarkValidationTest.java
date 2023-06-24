package com.alfonsoristorato.bookmarksproducer.service.validation;

import com.alfonsoristorato.bookmarksproducer.service.models.BookmarkBody;
import com.alfonsoristorato.common.utils.exceptions.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
public class SaveBookmarkValidationTest {
    @InjectMocks
    private SaveBookmarkValidation saveBookmarkValidation;

    @ParameterizedTest
    @ValueSource(strings = {"1", "20", "2100"})
    void validateRequest_shouldNotThrowBadRequestExceptionIfVideoIdIsAValidNumberAndAProperBookmarkBodyIsPassed(String videoId) {
        assertThatNoException().isThrownBy(() -> saveBookmarkValidation.validateRequest(videoId, new BookmarkBody(10)));

    }

    @ParameterizedTest
    @ValueSource(strings = {"", "notANumber", " 0 1", " 1", "1 "})
    void validateRequest_shouldThrowBadRequestExceptionIfVideoIdIsNotAValidNumber(String videoId) {
        assertThatThrownBy(() -> saveBookmarkValidation.validateRequest(videoId, new BookmarkBody(10)))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("videoId needs to be a valid number.");

    }

    @Test
    void validateRequest_shouldThrowBadRequestExceptionIfBookmarkBodyIsNull() {
        assertThatThrownBy(() -> saveBookmarkValidation.validateRequest("1", null))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("request body cannot be null.");

    }

    @Test
    void validateRequest_shouldThrowBadRequestExceptionIfBookmarkBodyPropertyIsInvalid() {
        assertThatThrownBy(() -> saveBookmarkValidation.validateRequest("1", new BookmarkBody(-10)))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("bookmarkPosition needs to be a number.");

    }

    @Test
    void validateHeaders_shouldNotThrowBadRequestExceptionIfValidAccountIdAndUserIdProvided() {
        String accountId = UUID.randomUUID().toString();
        String userId = UUID.randomUUID().toString();
        assertThatNoException().isThrownBy(() -> saveBookmarkValidation.validateHeaders(accountId, userId));
    }

    @ParameterizedTest
    @CsvSource({
            "accountId, 200dd54b-e006-4841-9b54-c30157c38efd, Invalid 'accountId' format provided.",
            "acc-ou-nt-Id, us-e-r-Id, Invalid 'accountId' format provided.",
            "200dd54b-e006-4841-9b54-c30157c38efd, userId, Invalid 'userId' format provided."
    })
    void validateHeaders_shouldThrowBadRequestExceptionIfAccountIdOrUserIdAreInvalid(String accountId, String userId, String exceptionMessage) {
        assertThatThrownBy(() -> saveBookmarkValidation.validateHeaders(accountId,userId))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(exceptionMessage);
    }
}
