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
        String signature = "signature";
        assertThatNoException().isThrownBy(() -> headerValidation.validateHeaders(accountId, userId, signature));
    }

    @ParameterizedTest
    @CsvSource({
            "accountId, 200dd54b-e006-4841-9b54-c30157c38efd, signature, Invalid 'accountId' format provided.",
            "acc-ou-nt-Id, us-e-r-Id, signature, Invalid 'accountId' format provided.",
            "200dd54b-e006-4841-9b54-c30157c38efd, userId, signature, Invalid 'userId' format provided.",
            "200dd54b-e006-4841-9b54-c30157c38efd, 200dd54b-e006-4841-9b54-c30157c38efd, , Invalid 'signature' format provided."
    })
    void validateHeaders_shouldThrowBadRequestExceptionIfParametersPassedAreInvalid(String accountId, String userId, String signature, String exceptionMessage) {
        assertThatThrownBy(() -> headerValidation.validateHeaders(accountId, userId, signature))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(exceptionMessage);
    }
}
