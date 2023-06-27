package com.alfonsoristorato.common.signatureverifier.health;

import com.alfonsoristorato.common.signatureverifier.config.SignatureVerifierConfigProperties;
import com.alfonsoristorato.common.signatureverifier.service.SignatureVerifierService;
import com.alfonsoristorato.common.utils.exceptions.BadRequestError;
import com.alfonsoristorato.common.utils.exceptions.BadRequestException;
import com.alfonsoristorato.common.utils.exceptions.DownstreamError;
import com.alfonsoristorato.common.utils.exceptions.DownstreamException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SignatureVerifierHealthIndicatorTest {
    @InjectMocks
    private SignatureVerifierHealthIndicator signatureVerifierHealthIndicator;

    @Mock
    private SignatureVerifierService signatureVerifierService;

    @Mock
    private SignatureVerifierConfigProperties signatureVerifierConfigProperties;

    private static Stream<Arguments> exceptionsReturned() {
        return Stream.of(
                Arguments.of(new BadRequestException(BadRequestError.BAD_REQUEST_ERROR("bad request error"))),
                Arguments.of(new DownstreamException(DownstreamError.DOWNSTREAM_ERROR("downstream error"))),
                Arguments.of(new RuntimeException("runtime exception"))

        );
    }

    @Test
    void health_shouldReturnUpWhenTheServiceReturnsOk() {
        when(signatureVerifierService.getHealth(any())).thenReturn(Mono.just(ResponseEntity.noContent().build()));

        StepVerifier.create(signatureVerifierHealthIndicator.health())
                .expectNextMatches(health -> health.getStatus().equals(Health.up().build().getStatus()))
                .verifyComplete();
    }

    @ParameterizedTest
    @MethodSource("exceptionsReturned")
    void health_shouldReturnDownWhenTheServiceThrowsAnException(RuntimeException exception) {
        when(signatureVerifierService.getHealth(any())).thenReturn(Mono.error(exception));

        StepVerifier.create(signatureVerifierHealthIndicator.health())
                .expectNextMatches(health -> health.getStatus().equals(Health.down().build().getStatus()))
                .verifyComplete();
    }
}
