CREATE TABLE IF NOT EXISTS video_bookmarks.bookmarks (
    accountId text,
    userId text,
    videoId text,
    bookmarkPosition int,
    timestamp bigint,
    PRIMARY KEY ((accountId), userId, videoId))
    WITH CLUSTERING ORDER BY (userId ASC, videoId ASC);;