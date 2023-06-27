package com.alfonsoristorato.common.signatureverifier.config;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class SignatureVerifierConfigPropertiesTest {

    private Validator validator;

    @BeforeEach
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    @NullSource
    void shouldThrowException_whenBaseUrkIsBlankOrNull(String baseUrl) {
        SignatureVerifierConfigProperties properties = new SignatureVerifierConfigProperties(baseUrl, "verifyPath", "healthPath");
        Set<ConstraintViolation<SignatureVerifierConfigProperties>> violations = validator.validate(properties);
        assertThat(violations)
                .isNotEmpty()
                .hasSize(1);

        ConstraintViolation<SignatureVerifierConfigProperties> violation = violations.iterator().next();
        assertThat(violation.getMessage()).isEqualTo("must not be blank");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    @NullSource
    void shouldThrowException_whenVerifyPathIsBlankOrNull(String verifyPath) {
        SignatureVerifierConfigProperties properties = new SignatureVerifierConfigProperties("baseUrl", verifyPath, "healthPath");
        Set<ConstraintViolation<SignatureVerifierConfigProperties>> violations = validator.validate(properties);
        assertThat(violations)
                .isNotEmpty()
                .hasSize(1);

        ConstraintViolation<SignatureVerifierConfigProperties> violation = violations.iterator().next();
        assertThat(violation.getMessage()).isEqualTo("must not be blank");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    @NullSource
    void shouldThrowException_whenHealthPathIsBlankOrNull(String healthPath) {
        SignatureVerifierConfigProperties properties = new SignatureVerifierConfigProperties("baseUrl", "verifyPath", healthPath);
        Set<ConstraintViolation<SignatureVerifierConfigProperties>> violations = validator.validate(properties);
        assertThat(violations)
                .isNotEmpty()
                .hasSize(1);

        ConstraintViolation<SignatureVerifierConfigProperties> violation = violations.iterator().next();
        assertThat(violation.getMessage()).isEqualTo("must not be blank");
    }

}
