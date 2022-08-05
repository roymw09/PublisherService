package org.ac.cst8277.williams.roy.service;

import lombok.extern.slf4j.Slf4j;
import org.ac.cst8277.williams.roy.model.Publisher;
import org.ac.cst8277.williams.roy.repository.PublisherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@Transactional
public class PublisherService {

    @Autowired
    private PublisherRepository publisherRepository;

    public Mono<Publisher> createPublisher(Publisher publisher) {
        return publisherRepository.save(publisher);
    }

    public Mono<Publisher> findPublisherById(String publisherId) {
        return publisherRepository.findById(publisherId);
    }
}
