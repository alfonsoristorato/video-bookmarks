package com.alfonsoristorato.common.signatureverifier.health;

import com.alfonsoristorato.common.signatureverifier.config.SignatureVerifierConfigProperties;
import com.alfonsoristorato.common.signatureverifier.service.SignatureVerifierService;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component("SignatureVerifierHealthIndicator")
public class SignatureVerifierHealthIndicator implements ReactiveHealthIndicator {

    private final SignatureVerifierConfigProperties signatureVerifierConfigProperties;
    private final SignatureVerifierService signatureVerifierService;

    public SignatureVerifierHealthIndicator(SignatureVerifierConfigProperties signatureVerifierConfigProperties, SignatureVerifierService signatureVerifierService) {
        this.signatureVerifierConfigProperties = signatureVerifierConfigProperties;
        this.signatureVerifierService = signatureVerifierService;
    }

    @Override
    public Mono<Health> health() {
        return signatureVerifierService
                .getHealth(signatureVerifierConfigProperties.healthPath())
                .flatMap(response -> Mono.just(Health.up().build()))
                .onErrorResume(throwable -> Mono.just(Health.down().build()));
    }
}
