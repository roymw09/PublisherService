package org.ac.cst8277.williams.roy.service;

import lombok.extern.slf4j.Slf4j;
import org.ac.cst8277.williams.roy.model.Content;
import org.ac.cst8277.williams.roy.model.Publisher;
import org.ac.cst8277.williams.roy.repository.ContentRepository;
import org.ac.cst8277.williams.roy.repository.PublisherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@Transactional
public class PublisherService {

    @Autowired
    private PublisherRepository publisherRepository;

    @Autowired
    private ContentRepository contentRepository;

    public Mono<Publisher> createPublisher(Publisher publisher) {
        return publisherRepository.save(publisher);
    }

    public Mono<Content> createContent(Content content) {
        return contentRepository.save(content);
    }

    public Flux<Content> getAllContent() { return contentRepository.findAll(); }

    public Mono<Publisher> findPublisherById(String publisherId) {
        return publisherRepository.findById(publisherId);
    }

    public Flux<Content> findContentByPublisherId(String publisherId) {
        return contentRepository.findContentByPublisherId(publisherId);
    }

    public Mono<Content> findContentById(Integer contentId) {
        return contentRepository.findById(contentId);
    }

    public Mono<Publisher> getPublisherToken(Integer userId) {
        return publisherRepository.getPublisherToken(userId);
    }
}
