package com.alfonsoristorato.bookmarksproducer.validation;

import com.alfonsoristorato.bookmarksproducer.exceptions.BadRequestException;
import com.alfonsoristorato.bookmarksproducer.models.BookmarkBody;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

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
}
