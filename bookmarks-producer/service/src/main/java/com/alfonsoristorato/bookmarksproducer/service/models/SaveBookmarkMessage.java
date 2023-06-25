package com.alfonsoristorato.bookmarksproducer.service.models;

import java.time.Instant;

public record SaveBookmarkMessage(String accountId, String userId, Integer videoId, Integer bookmarkPosition,
                                  Instant timestamp) {
}
