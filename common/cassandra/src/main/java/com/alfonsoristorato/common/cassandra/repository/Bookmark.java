package com.alfonsoristorato.common.cassandra.repository;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("bookmarks")
public record Bookmark (
        @PrimaryKey BookmarkPrimaryKey bookmarkPrimaryKey,
        int bookmarkPosition,
        long timestamp) {
}
