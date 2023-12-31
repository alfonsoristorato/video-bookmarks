package com.alfonsoristorato.bookmarksproducer.app.validation;

import com.alfonsoristorato.bookmarksproducer.app.models.BookmarkBody;
import com.alfonsoristorato.common.utils.exceptions.BadRequestError;
import com.alfonsoristorato.common.utils.exceptions.BadRequestException;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

@Component
public class ProducerValidation {

    public void validateRequest(String videoId, BookmarkBody bookmarkBody) {
        if (!NumberUtils.isCreatable(videoId)) {
            throw new BadRequestException(BadRequestError.BAD_REQUEST_ERROR("videoId needs to be a valid number."));
        }
        if (bookmarkBody.bookmarkPosition() == null) {
            throw new BadRequestException(BadRequestError.BAD_REQUEST_ERROR("bookmarkPosition cannot be null."));
        }
        if (bookmarkBody.bookmarkPosition() < 0) {
            throw new BadRequestException(BadRequestError.BAD_REQUEST_ERROR("bookmarkPosition needs to be a number."));
        }
    }

}
