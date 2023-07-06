package com.alfonsoristorato.common.cassandra.repository;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("bookmarks")
public class Bookmark {

    @PrimaryKey
    private BookmarkPrimaryKey bookmarkPrimaryKey;

    private int bookmarkPosition;

    private long timestamp;

    public Bookmark(BookmarkPrimaryKey bookmarkPrimaryKey, int bookmarkPosition, long timestamp) {
        this.bookmarkPrimaryKey = bookmarkPrimaryKey;
        this.bookmarkPosition = bookmarkPosition;
        this.timestamp = timestamp;
    }
}
