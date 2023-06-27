package com.alfonsoristorato.common.signatureverifier.service;

import com.alfonsoristorato.common.signatureverifier.config.SignatureVerifierConfigProperties;
import com.alfonsoristorato.common.utils.exceptions.BadRequestError;
import com.alfonsoristorato.common.utils.exceptions.BadRequestException;
import com.alfonsoristorato.common.utils.exceptions.DownstreamError;
import com.alfonsoristorato.common.utils.exceptions.DownstreamException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.core.publisher.Mono;

@Configuration
public class WebClientConfig {
    private final SignatureVerifierConfigProperties signatureVerifierConfigProperties;

    public WebClientConfig(SignatureVerifierConfigProperties signatureVerifierConfigProperties) {
        this.signatureVerifierConfigProperties = signatureVerifierConfigProperties;
    }

    @Bean
    SignatureVerifierService signatureVerifierService() {
        WebClient webClient = WebClient.builder()
                .baseUrl(signatureVerifierConfigProperties.baseUrl())
                .defaultStatusHandler(
                        HttpStatusCode::is4xxClientError, response4xx ->
                                Mono.just(new BadRequestException(BadRequestError.BAD_REQUEST_ERROR("signature is invalid.")))

                        )
                .defaultStatusHandler(
                        HttpStatusCode::is5xxServerError, response5xx ->
                                Mono.just(new DownstreamException(DownstreamError.DOWNSTREAM_ERROR("SignatureVerifier is down.")))
                )
                .build();

        HttpServiceProxyFactory httpServiceProxyFactory =
                HttpServiceProxyFactory
                        .builder(WebClientAdapter.forClient(webClient))

                        .build();
        return httpServiceProxyFactory.createClient(SignatureVerifierService.class);
    }
}
