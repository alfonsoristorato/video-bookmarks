package com.alfonsoristorato.bookmarksproducer.exceptions;

public record BadRequestError(String description, String details) {
    public static BadRequestError BAD_REQUEST_ERROR(String details) {
        return new BadRequestError("Bad Request Error", details);
    }

}
