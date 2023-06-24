package com.alfonsoristorato.common.utils.exceptions;

public class BadRequestException extends RuntimeException{
    private final BadRequestError error;

    public BadRequestException(BadRequestError error) {
        super(error.details());
        this.error = error;
    }

    public BadRequestError getError(){return error;}
}
