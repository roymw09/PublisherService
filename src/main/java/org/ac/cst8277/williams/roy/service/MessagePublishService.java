package org.ac.cst8277.williams.roy.service;

import org.ac.cst8277.williams.roy.model.Content;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class MessagePublishService {

    private String API_ENDPOINT = "http://localhost:8083/pub/content/";
    private WebClient webClient;

    @Autowired
    private ReactiveRedisOperations<String, Content> redisTemplate;

    public void publish(Integer contentId) {
        this.webClient = WebClient.builder()
                .baseUrl(API_ENDPOINT + contentId)
                .build();

        this.webClient.get()
                .retrieve()
                .bodyToMono(Content.class)
                .flatMap(content -> this.redisTemplate.convertAndSend("messages", content))
                .subscribe();
    }
}
