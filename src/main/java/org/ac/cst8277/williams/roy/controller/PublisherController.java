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

    @Autowired
    PublisherService publisherService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Publisher> createPublisher(@RequestBody Publisher publisher) {
        publisherService.createPublisher(publisher).subscribe();
        return new ResponseEntity<>(publisher, HttpStatus.CREATED);
    }

    @GetMapping("/{publisherId}")
    public Mono<ResponseEntity<Publisher>> findPublisherById(@PathVariable String publisherId) {
        Mono<Publisher> publisher = publisherService.findPublisherById(publisherId);
        return publisher.map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
