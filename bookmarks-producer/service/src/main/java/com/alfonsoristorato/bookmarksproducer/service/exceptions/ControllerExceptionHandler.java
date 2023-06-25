package com.alfonsoristorato.bookmarksproducer.service.exceptions;

import com.alfonsoristorato.common.utils.exceptions.BadRequestError;
import com.alfonsoristorato.common.utils.exceptions.GlobalExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
@Import(GlobalExceptionHandler.class)
public class ControllerExceptionHandler {
    private final Logger log = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<BadRequestError> httpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.error("Caught the following exception: {} with message: {}", ex.getClass(), ex.getMessage());
        return ResponseEntity.badRequest().body(BadRequestError.BAD_REQUEST_ERROR("Required request body is missing."));
    }

}
