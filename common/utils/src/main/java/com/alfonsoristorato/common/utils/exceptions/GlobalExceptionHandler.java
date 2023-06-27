package com.alfonsoristorato.common.utils.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {
    private final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<BadRequestError> missingHeaderException(MissingRequestHeaderException ex) {
        log.error("Caught the following exception: {} with message: {}", ex.getClass(), ex.getMessage());
        String missingHeader = ex.getHeaderName();
        return ResponseEntity.badRequest().body(BadRequestError.BAD_REQUEST_ERROR(String.format("Required request header '%s' missing.", missingHeader)));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<BadRequestError> endpointNotHandledException(NoHandlerFoundException ex){
        log.error("Caught the following exception: {} with message: {}", ex.getClass(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(BadRequestError.BAD_REQUEST_ERROR(ex.getMessage()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<BadRequestError> badRequestException(BadRequestException ex) {
        log.error("Caught the following exception: {} with message: {}", ex.getClass(), ex.getMessage());
        return ResponseEntity.badRequest().body(ex.getError());
    }

    @ExceptionHandler(DownstreamException.class)
    public ResponseEntity<DownstreamError> downstreamException(DownstreamException ex) {
        log.error("Caught the following exception: {} with message: {}", ex.getClass(), ex.getMessage());
        return ResponseEntity.internalServerError().body(ex.getError());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<BadRequestError> methodNotSupportedException(HttpRequestMethodNotSupportedException ex){
        log.error("Caught the following exception: {} with message: {}", ex.getClass(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(BadRequestError.BAD_REQUEST_ERROR(ex.getMessage()));
    }
}
