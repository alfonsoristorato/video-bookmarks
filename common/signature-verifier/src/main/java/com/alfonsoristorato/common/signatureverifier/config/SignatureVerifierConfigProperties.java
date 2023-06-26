package com.alfonsoristorato.common.signatureverifier.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Name;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "downstreams.signature-verifier.urls")
@Validated
public record SignatureVerifierConfigProperties(
        @NotBlank @Name("verify") String verifyUrl,
        @NotBlank @Name("health") String healthUrl) {
}
