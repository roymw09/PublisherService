package org.ac.cst8277.williams.roy.controller;

import org.ac.cst8277.williams.roy.model.Content;
import org.ac.cst8277.williams.roy.service.ContentService;
import org.ac.cst8277.williams.roy.service.RedisMessagePublishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/pub/content")
public class ContentController {

    @Autowired
    ContentService contentService;

    @Autowired
    RedisMessagePublishService redisMessagePublishService; // this service is used to publish messages to the redis channel

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Content> createContent(@RequestBody Content content) {
        Mono<Content> savedContent = contentService.createContent(content); // saves the content in the db
        return savedContent.mapNotNull(message -> {
            if (message != null && message.getId() != null) {
                redisMessagePublishService.initWebClient(message.getId());
                redisMessagePublishService.publish(); // publish content to the redis 'messages' channel
            }
            return message;
        });
    }

    @GetMapping("/find/{publisherId}")
    public Flux<Content> findContentByPublisherId(@PathVariable String publisherId) {
        return contentService.findContentByPublisherId(publisherId);
    }

    @GetMapping("/{contentId}")
    public Mono<ResponseEntity<Content>> findContentById(@PathVariable Integer contentId) {
        Mono<Content> content = contentService.findContentById(contentId);
        return content.map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/findAll")
    public Flux<Content> findAllContent() {
        return contentService.getAllContent();
    }
}
