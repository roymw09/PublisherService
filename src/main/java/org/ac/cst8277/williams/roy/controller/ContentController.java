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

    @PostMapping("/create/{token}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Content> createContent(@PathVariable("token") String token, @RequestBody Content content) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = new RestTemplate().exchange("http://localhost:8081/authenticate/validate", HttpMethod.GET, request, String.class);
        // save & publish the content if users JWT is valid
        if (response.getStatusCode() == HttpStatus.OK) {
            Mono<Content> savedContent = contentService.createContent(content); // saves the content in the db
            savedContent.mapNotNull(message -> {
                redisMessagePublishService.initWebClient(message.getId());
                redisMessagePublishService.publish(); // publish content to the redis 'messages' channel
               return message;
            }).subscribe();
            return new ResponseEntity<>(content, response.getStatusCode());
        }
        return new ResponseEntity<>(null, response.getStatusCode());
    }

    @GetMapping("/find/{publisherId}")
    public Flux<Content> findContentByPublisherId(@PathVariable Integer publisherId) {
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
