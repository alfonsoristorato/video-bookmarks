package com.alfonsoristorato.bookmarksproducer.app.validation;

import com.alfonsoristorato.bookmarksproducer.app.models.BookmarkBody;
import com.alfonsoristorato.common.utils.exceptions.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
public class ProducerValidationTest {
    @InjectMocks
    private ProducerValidation producerValidation;

    @ParameterizedTest
    @ValueSource(strings = {"1", "20", "2100"})
    void validateRequest_shouldNotThrowBadRequestExceptionIfVideoIdIsAValidNumberAndAProperBookmarkBodyIsPassed(String videoId) {
        assertThatNoException().isThrownBy(() -> producerValidation.validateRequest(videoId, new BookmarkBody(10)));

    }

    @ParameterizedTest
    @ValueSource(strings = {"", "notANumber", " 0 1", " 1", "1 "})
    void validateRequest_shouldThrowBadRequestExceptionIfVideoIdIsNotAValidNumber(String videoId) {
        assertThatThrownBy(() -> producerValidation.validateRequest(videoId, new BookmarkBody(10)))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("videoId needs to be a valid number.");

    }

    @Test
    void validateRequest_shouldThrowBadRequestExceptionIfBookmarkBodyPropertyIsInvalid() {
        assertThatThrownBy(() -> producerValidation.validateRequest("1", new BookmarkBody(-10)))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("bookmarkPosition needs to be a number.");

    }
}
