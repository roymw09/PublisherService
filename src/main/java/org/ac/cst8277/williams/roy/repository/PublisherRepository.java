package org.ac.cst8277.williams.roy.repository;

import org.ac.cst8277.williams.roy.model.Publisher;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface PublisherRepository extends ReactiveCrudRepository<Publisher, Integer> {
}
