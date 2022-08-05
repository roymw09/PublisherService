package org.ac.cst8277.williams.roy.service;

import org.ac.cst8277.williams.roy.model.Content;
import org.ac.cst8277.williams.roy.repository.ContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Transactional
public class ContentService {

    private final ContentRepository contentRepository;

    public ContentService(ContentRepository contentRepository) {
        this.contentRepository = contentRepository;
    }

    public Mono<Content> createContent(Content content) {
        return contentRepository.save(content);
    }

    public Flux<Content> getAllContent() { return contentRepository.findAll(); }

    public Flux<Content> findContentByPublisherId(Integer publisherId) {
        return contentRepository.findContentByPublisherId(publisherId);
    }

    public Mono<Content> findContentById(Integer contentId) {
        return contentRepository.findById(contentId);
    }
}
