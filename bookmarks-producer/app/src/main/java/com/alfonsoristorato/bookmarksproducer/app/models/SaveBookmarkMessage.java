package com.alfonsoristorato.bookmarksproducer.app.models;

import java.time.Instant;

public record SaveBookmarkMessage(
        String accountId,
        String userId,
        Integer videoId,
        Integer bookmarkPosition,
        Instant timestamp) {
}
