package com.alfonsoristorato.bookmarksproducer.app.controllers;

import com.alfonsoristorato.bookmarksproducer.app.models.BookmarkBody;
import com.alfonsoristorato.bookmarksproducer.app.service.ProducerService;
import com.alfonsoristorato.bookmarksproducer.app.validation.ProducerValidation;
import com.alfonsoristorato.common.signatureverifier.config.SignatureVerifierConfigProperties;
import com.alfonsoristorato.common.signatureverifier.service.SignatureVerifierService;
import com.alfonsoristorato.common.utils.validation.HeaderValidation;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@Controller
@RequestMapping(path = "/bookmark")
public class ProducerController {

    private final ProducerValidation producerValidation;

    private final HeaderValidation headerValidation;

    private final ProducerService producerService;

    private final SignatureVerifierService signatureVerifierService;

    private final SignatureVerifierConfigProperties signatureVerifierConfigProperties;

    public ProducerController(ProducerValidation producerValidation, HeaderValidation headerValidation, ProducerService producerService, SignatureVerifierService signatureVerifierService, SignatureVerifierConfigProperties signatureVerifierConfigProperties) {
        this.producerValidation = producerValidation;
        this.headerValidation = headerValidation;
        this.producerService = producerService;
        this.signatureVerifierService = signatureVerifierService;
        this.signatureVerifierConfigProperties = signatureVerifierConfigProperties;
    }


    //accountId
    //userId
    //videoId
    //bookmarkPosition
    //timeStamp

    @PutMapping(path = "/{videoId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Void>> produceBookmark(
            @PathVariable String videoId,
            @RequestBody BookmarkBody bookmarkBody,
            @RequestHeader String accountId,
            @RequestHeader String userId,
            @RequestHeader String signature
    ) {
        headerValidation.validateHeaders(accountId, userId, signature);
        producerValidation.validateRequest(videoId, bookmarkBody);
        return signatureVerifierService
                .verifySignature(signatureVerifierConfigProperties.verifyPath(), signature)
                .flatMap(verifyResponseSuccess -> {
                    producerService.sendKafkaMessage(accountId, userId, Integer.parseInt(videoId), bookmarkBody.bookmarkPosition());
                    return Mono.just(ResponseEntity.accepted().build());
                });
    }

}
