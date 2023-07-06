package com.alfonsoristorato.common.cassandra.repository;

import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface BookmarksRepository extends ReactiveCassandraRepository<Bookmark, BookmarkPrimaryKey> {

    @Override
    <S extends Bookmark> Mono<S> save(@NonNull S entity);
}

