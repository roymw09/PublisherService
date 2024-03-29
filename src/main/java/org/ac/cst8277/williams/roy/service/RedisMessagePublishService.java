package org.ac.cst8277.williams.roy.service;

import org.ac.cst8277.williams.roy.model.Content;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class RedisMessagePublishService implements RedisPublishService {

    private final String API_ENDPOINT = "http://localhost:8080/pub/content/";
    private WebClient webClient;
    private final ReactiveRedisOperations<String, Content> contentTemplate;

    public RedisMessagePublishService(ReactiveRedisOperations<String, Content> contentTemplate) {
        this.contentTemplate = contentTemplate;
    }

    @Override
    public void initWebClient(Integer id) {
        this.webClient = WebClient.builder()
                .baseUrl(API_ENDPOINT + id)
                .build();
    }

    @Override
    public void publish() {
        this.webClient.get()
                .retrieve()
                .bodyToMono(Content.class)
                .flatMap(content -> this.contentTemplate.convertAndSend("messages", content))
                .subscribe();
    }
}
