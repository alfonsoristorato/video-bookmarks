package com.alfonsoristorato.bookmarksproducer.app.controllers;

import com.alfonsoristorato.bookmarksproducer.app.models.BookmarkBody;
import com.alfonsoristorato.bookmarksproducer.app.service.SaveBookmarkService;
import com.alfonsoristorato.bookmarksproducer.app.validation.SaveBookmarkValidation;
import com.alfonsoristorato.common.utils.validation.HeaderValidation;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping(path = "/bookmark")
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
