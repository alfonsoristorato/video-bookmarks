package com.alfonsoristorato.common.utils.exceptions;

public record DownstreamError(String description, String details) {
    public static DownstreamError DOWNSTREAM_ERROR(String details) {
        return new DownstreamError("Downstream Error", details);
    }
}
