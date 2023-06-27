package com.alfonsoristorato.common.utils.exceptions;

public class DownstreamException extends RuntimeException{
    private final DownstreamError error;

    public DownstreamException(DownstreamError error) {
        super(error.details());
        this.error = error;
    }

    public DownstreamError getError(){return error;}
}
