package com.alfonsoristorato.bookmarksproducer.app.controllers;

import com.alfonsoristorato.bookmarksproducer.app.models.BookmarkBody;
import com.alfonsoristorato.bookmarksproducer.app.service.SaveBookmarkService;
import com.alfonsoristorato.bookmarksproducer.app.validation.SaveBookmarkValidation;
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
public class SaveBookmarkController {

    private final SaveBookmarkValidation saveBookmarkValidation;

    private final HeaderValidation headerValidation;

    private final SaveBookmarkService saveBookmarkService;

    private final SignatureVerifierService signatureVerifierService;

    private final SignatureVerifierConfigProperties signatureVerifierConfigProperties;

    public SaveBookmarkController(SaveBookmarkValidation saveBookmarkValidation, HeaderValidation headerValidation, SaveBookmarkService saveBookmarkService, SignatureVerifierService signatureVerifierService, SignatureVerifierConfigProperties signatureVerifierConfigProperties) {
        this.saveBookmarkValidation = saveBookmarkValidation;
        this.headerValidation = headerValidation;
        this.saveBookmarkService = saveBookmarkService;
        this.signatureVerifierService = signatureVerifierService;
        this.signatureVerifierConfigProperties = signatureVerifierConfigProperties;
    }


    //accountId
    //userId
    //videoId
    //bookmarkPosition
    //timeStamp

    @PutMapping(path = "/{videoId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Void>> saveBookmark(
            @PathVariable String videoId,
            @RequestBody BookmarkBody bookmarkBody,
            @RequestHeader String accountId,
            @RequestHeader String userId,
            @RequestHeader String signature
    ) {
        headerValidation.validateHeaders(accountId, userId, signature);
        saveBookmarkValidation.validateRequest(videoId, bookmarkBody);
        return signatureVerifierService
                .verifySignature(signatureVerifierConfigProperties.verifyPath(), signature)
                .flatMap(verifyResponseSuccess -> {
                    saveBookmarkService.sendKafkaMessage(accountId, userId, Integer.parseInt(videoId), bookmarkBody.bookmarkPosition());
                    return Mono.just(ResponseEntity.accepted().build());
                });
    }

}
