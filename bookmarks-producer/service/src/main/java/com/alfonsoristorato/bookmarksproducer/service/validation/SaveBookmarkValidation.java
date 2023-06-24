package com.alfonsoristorato.bookmarksproducer.service.validation;

import com.alfonsoristorato.bookmarksproducer.service.models.BookmarkBody;
import com.alfonsoristorato.common.utils.exceptions.BadRequestError;
import com.alfonsoristorato.common.utils.exceptions.BadRequestException;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

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

    public void validateHeaders(String accountId, String userId) {
        Pattern UUID_REGEX =
                Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
        boolean accountIdValid = UUID_REGEX.matcher(accountId).matches();
        if(!accountIdValid) throw new BadRequestException(BadRequestError.BAD_REQUEST_ERROR("Invalid 'accountId' format provided."));
        boolean userIdValid = UUID_REGEX.matcher(userId).matches();
        if(!userIdValid) throw new BadRequestException(BadRequestError.BAD_REQUEST_ERROR("Invalid 'userId' format provided."));
    }
}
