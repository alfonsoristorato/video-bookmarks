package com.alfonsoristorato.common.utils.validation;

import com.alfonsoristorato.common.utils.exceptions.BadRequestError;
import com.alfonsoristorato.common.utils.exceptions.BadRequestException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class HeaderValidation {


    public void validateHeaders(String accountId, String userId, String signature) {
        Pattern UUID_REGEX =
                Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
        boolean accountIdValid = UUID_REGEX.matcher(accountId).matches();
        if(!accountIdValid) throw new BadRequestException(BadRequestError.BAD_REQUEST_ERROR("Invalid 'accountId' format provided."));
        boolean userIdValid = UUID_REGEX.matcher(userId).matches();
        if(!userIdValid) throw new BadRequestException(BadRequestError.BAD_REQUEST_ERROR("Invalid 'userId' format provided."));
        boolean signatureValid = StringUtils.isNotBlank(signature);
        if(!signatureValid) throw new BadRequestException(BadRequestError.BAD_REQUEST_ERROR("Invalid 'signature' format provided."));
    }
}
