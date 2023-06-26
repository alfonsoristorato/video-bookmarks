package com.alfonsoristorato.common.utils.validation;

import com.alfonsoristorato.common.utils.exceptions.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
@ExtendWith(MockitoExtension.class)
public class HeaderValidationTest {
    @InjectMocks
    private HeaderValidation headerValidation;

    @Test
    void validateHeaders_shouldNotThrowBadRequestExceptionIfValidAccountIdAndUserIdProvided() {
        String accountId = UUID.randomUUID().toString();
        String userId = UUID.randomUUID().toString();
        assertThatNoException().isThrownBy(() -> headerValidation.validateHeaders(accountId, userId));
    }

    @ParameterizedTest
    @CsvSource({
            "accountId, 200dd54b-e006-4841-9b54-c30157c38efd, Invalid 'accountId' format provided.",
            "acc-ou-nt-Id, us-e-r-Id, Invalid 'accountId' format provided.",
            "200dd54b-e006-4841-9b54-c30157c38efd, userId, Invalid 'userId' format provided."
    })
    void validateHeaders_shouldThrowBadRequestExceptionIfAccountIdOrUserIdAreInvalid(String accountId, String userId, String exceptionMessage) {
        assertThatThrownBy(() -> headerValidation.validateHeaders(accountId, userId))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(exceptionMessage);
    }
}
