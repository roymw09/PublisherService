package org.ac.cst8277.williams.roy.controller;

import org.ac.cst8277.williams.roy.model.Content;
import org.ac.cst8277.williams.roy.model.Publisher;
import org.ac.cst8277.williams.roy.model.User;
import org.ac.cst8277.williams.roy.service.MessagePublishService;
import org.ac.cst8277.williams.roy.service.PublisherService;
import org.ac.cst8277.williams.roy.service.TokenPublishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

@RestController
@RequestMapping("/pub")
public class PublisherController {

    @Autowired
    PublisherService publisherService; // publishers in this context refers to users with publishing rights

    @Autowired
    MessagePublishService messagePublishService; // this service is used to publish messages to the redis channel

    @Autowired
    TokenPublishService tokenPublishService; // this service is used to publish publisher tokens to the redis channel

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Publisher> createPublisher(@RequestBody Publisher publisher) {
        publisher.setId(UUID.randomUUID().toString());
        Mono<Publisher> savedPublisher = publisherService.createPublisher(publisher);
        return savedPublisher.mapNotNull(pub -> {
            if (pub != null && pub.getUser_id()!= null) {
                tokenPublishService.initWebClient(pub.getUser_id());
                tokenPublishService.publish();
            }
            return pub;
        });
    }

    @GetMapping("/{publisherId}")
    public Mono<ResponseEntity<Publisher>> findPublisherById(@PathVariable String publisherId) {
        Mono<Publisher> publisher = publisherService.findPublisherById(publisherId);
        return publisher.map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping("/content/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Content> createContent(@RequestBody Content content) {
        Mono<Content> savedContent = publisherService.createContent(content); // saves the content in the db
        return savedContent.mapNotNull(message -> {
            if (message != null && message.getId() != null)
            messagePublishService.initWebClient(message.getId());
            messagePublishService.publish(); // publish content to the redis 'messages' channel
            return message;
        });
    }

    @GetMapping("/content/find/{publisherId}")
    public Flux<Content> findContentByPublisherId(@PathVariable String publisherId) {
        return publisherService.findContentByPublisherId(publisherId);
    }

    @GetMapping("/content/{contentId}")
    public Mono<ResponseEntity<Content>> findContentById(@PathVariable Integer contentId) {
        Mono<Content> content = publisherService.findContentById(contentId);
        return content.map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/content/findAll")
    public Flux<Content> findAllContent() {
        return publisherService.getAllContent();
    }

    // check the users token through the UMS to verify that they have publishing rights
    @GetMapping("/verify/{email}/{token}")
    public ResponseEntity<User> checkUserToken(@PathVariable("email") String email, @PathVariable("token") String token) {
        ResponseEntity<User> restTemplate;
        try {
            restTemplate = new RestTemplate().getForEntity(
                    "http://usermanagement-service:8081/users/" + email + "/" + token, User.class);
        } catch (HttpClientErrorException e) {
            restTemplate = ResponseEntity.status(e.getRawStatusCode()).headers(e.getResponseHeaders()).body(null);
        }
        return restTemplate;
    }

    @GetMapping("/getToken/{userId}")
    public Mono<ResponseEntity<Publisher>> getPublisherToken(@PathVariable("userId") Integer userId) {
        Mono<Publisher> token = publisherService.getPublisherToken(userId);
        return token.map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
