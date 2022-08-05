package org.ac.cst8277.williams.roy.controller;

import org.ac.cst8277.williams.roy.model.Publisher;
import org.ac.cst8277.williams.roy.model.User;
import org.ac.cst8277.williams.roy.service.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/pub/publisher")
public class PublisherController {

    private final PublisherService publisherService;

    public PublisherController(PublisherService publisherService) {
        this.publisherService = publisherService;
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Publisher> createPublisher(@RequestBody Publisher publisher) {
        return publisherService.createPublisher(publisher);
    }

    @GetMapping("/{publisherId}")
    public Mono<Publisher> findPublisherById(@PathVariable String publisherId) {
        return publisherService.findPublisherById(publisherId);
    }
}
