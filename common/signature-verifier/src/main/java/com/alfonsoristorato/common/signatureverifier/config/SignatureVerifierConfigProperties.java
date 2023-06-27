package com.alfonsoristorato.common.signatureverifier.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Name;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "downstreams.signature-verifier")
@Validated
public record SignatureVerifierConfigProperties(
        @NotBlank @Name("base-url")String baseUrl,
        @NotBlank @Name("paths.verify") String verifyPath,
        @NotBlank @Name("paths.health") String healthPath) {
}
