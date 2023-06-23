package com.alfonsoristorato.bookmarksproducer.validation;

import com.alfonsoristorato.bookmarksproducer.exceptions.BadRequestError;
import com.alfonsoristorato.bookmarksproducer.exceptions.BadRequestException;
import com.alfonsoristorato.bookmarksproducer.models.BookmarkBody;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

@Component
public class SaveBookmarkValidation {

    public void validateRequest(String videoId, BookmarkBody bookmarkBody){
        if(!NumberUtils.isCreatable(videoId)){
            throw new BadRequestException(BadRequestError.BAD_REQUEST_ERROR("videoId needs to be a valid number."));
        }
        if(bookmarkBody == null){
            throw new BadRequestException(BadRequestError.BAD_REQUEST_ERROR("request body cannot be null."));
        }
        if(bookmarkBody.bookmarkPosition() == null){
            throw new BadRequestException(BadRequestError.BAD_REQUEST_ERROR("bookmarkPosition cannot be null."));
        }
        if(bookmarkBody.bookmarkPosition() < 0){
            throw new BadRequestException(BadRequestError.BAD_REQUEST_ERROR("bookmarkPosition needs to be a number."));
        }
    }
}
