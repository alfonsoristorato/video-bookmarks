package com.alfonsoristorato.common.cassandra.config;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class CassandraConfigPropertiesTest {

    private Validator validator;

    @BeforeEach
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }


    @ParameterizedTest
    @CsvSource(value = {
            "' ', port, localDatacenter, contactPoints, schemaAction",
            "'', port, localDatacenter, contactPoints, schemaAction",
            "nullValue, port, localDatacenter, contactPoints, schemaAction",
            "keyspaceName, '', localDatacenter, contactPoints, schemaAction",
            "keyspaceName, ' ', localDatacenter, contactPoints, schemaAction",
            "keyspaceName, nullValue, localDatacenter, contactPoints, schemaAction",
            "keyspaceName, port, '', contactPoints, schemaAction",
            "keyspaceName, port, ' ', contactPoints, schemaAction",
            "keyspaceName, port, nullValue, contactPoints, schemaAction",
            "keyspaceName, port, localDatacenter, '', schemaAction",
            "keyspaceName, port, localDatacenter, ' ', schemaAction",
            "keyspaceName, port, localDatacenter, nullValue, schemaAction",
            "keyspaceName, port, localDatacenter, contactPoints, ''",
            "keyspaceName, port, localDatacenter, contactPoints, ' '",
            "keyspaceName, port, localDatacenter, contactPoints, nullValue",
    }, nullValues = "nullValue")
    void shouldThrowException_whenAConfigIsBlankOrNull(String keyspaceName, String port, String localDatacenter, String contactPoints, String schemaAction) {
        CassandraConfigProperties properties = new CassandraConfigProperties(keyspaceName, port, localDatacenter, contactPoints, schemaAction);
        Set<ConstraintViolation<CassandraConfigProperties>> violations = validator.validate(properties);
        assertThat(violations)
                .isNotEmpty()
                .hasSize(1);

        ConstraintViolation<CassandraConfigProperties> violation = violations.iterator().next();
        assertThat(violation.getMessage()).isEqualTo("must not be blank");
    }
}
