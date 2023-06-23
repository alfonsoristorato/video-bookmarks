package com.alfonsoristorato.bookmarksproducer.controllers;

import com.alfonsoristorato.bookmarksproducer.models.BookmarkBody;
import com.alfonsoristorato.bookmarksproducer.validation.SaveBookmarkValidation;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping(path = "/bookmark")
public class SaveBookmarkController {

    private final SaveBookmarkValidation saveBookmarkValidation;

    public SaveBookmarkController(SaveBookmarkValidation saveBookmarkValidation) {
        this.saveBookmarkValidation = saveBookmarkValidation;
    }

    //accountId
    //userId
    //videoId
    //bookmarkPosition
    //timeStamp

    @PutMapping(path = "/{videoId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> saveBookmark(@PathVariable String videoId, @RequestBody BookmarkBody bookmarkBody){
        saveBookmarkValidation.validateRequest(videoId,bookmarkBody);
        return ResponseEntity.accepted().build();
    }

}
