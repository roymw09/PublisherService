package org.ac.cst8277.williams.roy.controller;

import org.ac.cst8277.williams.roy.model.Content;
import org.ac.cst8277.williams.roy.model.Publisher;
import org.ac.cst8277.williams.roy.model.User;
import org.ac.cst8277.williams.roy.service.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/pubService")
public class PublisherController {

    @Autowired
    PublisherService publisherService;

    @PostMapping("/publisher/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Publisher> createPublisher(@RequestBody Publisher publisher) {
        return publisherService.createPublisher(publisher);
    }

    @GetMapping("/publisher/{publisherId}")
    public Mono<ResponseEntity<Publisher>> findPublisherById(@PathVariable Integer publisherId) {
        Mono<Publisher> publisher = publisherService.findPublisherById(publisherId);
        return publisher.map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping("/content/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Content> createContent(@RequestBody Content content) {
        return publisherService.createContent(content);
    }

    @GetMapping("/content/find/{publisherId}")
    public Flux<Content> findContentByPublisherId(@PathVariable Integer publisherId) {
        return publisherService.findContentByPublisherId(publisherId);
    }

    @GetMapping("/content/{contentId}")
    public Mono<ResponseEntity<Content>> findContentById(@PathVariable Integer contentId) {
        Mono<Content> content = publisherService.findContentById(contentId);
        return content.map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    // check the users token through the UMS to verify that they have publishing rights
    @GetMapping("/verify/{email}/{token}")
    public ResponseEntity<User> checkUserToken(@PathVariable("email") String email, @PathVariable("token") String token) {
        ResponseEntity<User> restTemplate;
        try {
            restTemplate = new RestTemplate().getForEntity(
                    "http://localhost:8081/users/" + email + "/" + token, User.class);
        } catch (HttpClientErrorException e) {
            restTemplate = ResponseEntity.status(e.getRawStatusCode()).headers(e.getResponseHeaders()).body(null);
        }
        return restTemplate;
    }
}
