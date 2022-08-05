package org.ac.cst8277.williams.roy.controller;

import org.ac.cst8277.williams.roy.model.Content;
import org.ac.cst8277.williams.roy.service.ContentService;
import org.ac.cst8277.williams.roy.service.RedisMessagePublishService;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/pub/content")
public class ContentController {

    @Autowired
    private ContentService contentService;

    @Autowired
    private RedisMessagePublishService redisMessagePublishService; // this service is used to publish messages to the redis channel

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Content> createContent(@RequestBody Content content) {
        // save & publish the content
        Mono<Content> savedContent = contentService.createContent(content); // saves the content in the db
        savedContent.mapNotNull(message -> {
            redisMessagePublishService.initWebClient(message.getId());
            redisMessagePublishService.publish(); // publish content to the redis 'messages' channel
            return message;
        });
        return savedContent;
    }

    @GetMapping("/find/{publisherId}")
    public Flux<Content> findContentByPublisherId(@PathVariable Integer publisherId) {
        return contentService.findContentByPublisherId(publisherId);
    }

    @GetMapping("/{contentId}")
    public Mono<Content> findContentById(@PathVariable Integer contentId) {
        return contentService.findContentById(contentId);
    }

    @GetMapping("/findAll")
    public Flux<Content> findAllContent() {
        return contentService.getAllContent();
    }
}
