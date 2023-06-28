CREATE KEYSPACE IF NOT EXISTS video_bookmarks
    WITH REPLICATION = {
        'class' : 'SimpleStrategy',
        'replication_factor' : 1
        };