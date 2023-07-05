package com.alfonsoristorato.common.utils.models;

import java.time.Instant;

public record BookmarkMessage(
        String accountId,
        String userId,
        Integer videoId,
        Integer bookmarkPosition,
        Instant timestamp) {
}
