package com.alfonsoristorato.common.kafka.config;

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

public class KafkaTopicConfigPropertiesTest {

    private Validator validator;

    @BeforeEach
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    @NullSource
    void shouldThrowException_whenBookmarkTopicIsBlankOrNull(String bookmarkTopic) {
        KafkaTopicConfigProperties properties = new KafkaTopicConfigProperties(bookmarkTopic, "healthTopic");
        Set<ConstraintViolation<KafkaTopicConfigProperties>> violations = validator.validate(properties);
        assertThat(violations)
                .isNotEmpty()
                .hasSize(1);

        ConstraintViolation<KafkaTopicConfigProperties> violation = violations.iterator().next();
        assertThat(violation.getMessage()).isEqualTo("must not be blank");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    @NullSource
    void shouldThrowException_whenHealthTopicIsBlankOrNull(String healthTopic) {
        KafkaTopicConfigProperties properties = new KafkaTopicConfigProperties("bookmarkTopic", healthTopic);
        Set<ConstraintViolation<KafkaTopicConfigProperties>> violations = validator.validate(properties);
        assertThat(violations)
                .isNotEmpty()
                .hasSize(1);

        ConstraintViolation<KafkaTopicConfigProperties> violation = violations.iterator().next();
        assertThat(violation.getMessage()).isEqualTo("must not be blank");
    }

}
