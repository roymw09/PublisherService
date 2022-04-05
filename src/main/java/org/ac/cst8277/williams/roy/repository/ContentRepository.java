package org.ac.cst8277.williams.roy.repository;

import org.ac.cst8277.williams.roy.model.Content;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface ContentRepository extends ReactiveCrudRepository<Content, Integer> {
    @Query("SELECT * FROM content WHERE publisher_id = :publisherId")
    Flux<Content> findContentByPublisherId(@Param("publisherId") Integer publisherId);
}
