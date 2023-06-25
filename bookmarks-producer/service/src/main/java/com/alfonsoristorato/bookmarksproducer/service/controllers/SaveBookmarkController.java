package com.alfonsoristorato.bookmarksproducer.service.controllers;

import com.alfonsoristorato.bookmarksproducer.service.models.BookmarkBody;
import com.alfonsoristorato.bookmarksproducer.service.services.SaveBookmarkService;
import com.alfonsoristorato.bookmarksproducer.service.validation.SaveBookmarkValidation;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import validation.HeaderValidation;


@Controller
@RequestMapping(path = "/bookmark")
@Import(HeaderValidation.class)
public class SaveBookmarkController {

    private final SaveBookmarkValidation saveBookmarkValidation;

    private final HeaderValidation headerValidation;

    private final SaveBookmarkService saveBookmarkService;

    public SaveBookmarkController(SaveBookmarkValidation saveBookmarkValidation, HeaderValidation headerValidation, SaveBookmarkService saveBookmarkService) {
        this.saveBookmarkValidation = saveBookmarkValidation;
        this.headerValidation = headerValidation;
        this.saveBookmarkService = saveBookmarkService;
    }

    //accountId
    //userId
    //videoId
    //bookmarkPosition
    //timeStamp

    @PutMapping(path = "/{videoId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> saveBookmark(
            @PathVariable String videoId,
            @RequestBody BookmarkBody bookmarkBody,
            @RequestHeader String accountId,
            @RequestHeader String userId
    ) throws JsonProcessingException {
        headerValidation.validateHeaders(accountId, userId);
        saveBookmarkValidation.validateRequest(videoId, bookmarkBody);
        saveBookmarkService.sendKafkaMessage(accountId, userId, Integer.parseInt(videoId), bookmarkBody.bookmarkPosition());
        return ResponseEntity.accepted().build();
    }

}
