package com.alfonsoristorato.bookmarksproducer.service.exceptions;

public class BadRequestException extends RuntimeException{
    private final BadRequestError error;

    public BadRequestException(BadRequestError error) {
        super(error.details());
        this.error = error;
    }

    public BadRequestError getError(){return error;}
}
