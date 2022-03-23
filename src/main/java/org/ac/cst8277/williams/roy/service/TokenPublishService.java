package org.ac.cst8277.williams.roy.service;

import org.ac.cst8277.williams.roy.model.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class TokenPublishService implements RedisPublishService{

    private String API_ENDPOINT = "http://localhost:8083/pub/getToken/";
    private WebClient webClient;
    @Autowired
    private ReactiveRedisOperations<String, Publisher> tokenTemplate;

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
                .bodyToMono(Publisher.class)
                .flatMap(publisher -> this.tokenTemplate.convertAndSend("publisher_token", publisher))
                .subscribe();
    }
}
