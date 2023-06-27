package com.alfonsoristorato.common.signatureverifier.service;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Mono;

@HttpExchange(accept = MediaType.APPLICATION_JSON_VALUE, contentType = MediaType.APPLICATION_JSON_VALUE)
public interface SignatureVerifierService {
    @GetExchange("{healthPath}")
    Mono<ResponseEntity<Void>> getHealth(@PathVariable String healthPath);

    @PostExchange("{verifyPath}")
    Mono<ResponseEntity<Void>> verifySignature(@PathVariable String verifyPath, @RequestHeader String signature);
}
