package org.ac.cst8277.williams.roy.controller;

import lombok.extern.slf4j.Slf4j;
import org.ac.cst8277.williams.roy.model.Content;
import org.ac.cst8277.williams.roy.model.Publisher;
import org.ac.cst8277.williams.roy.repository.ContentRepository;
import org.ac.cst8277.williams.roy.repository.PublisherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@Slf4j
public class PublisherControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private PublisherRepository publisherRepository;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private DatabaseClient databaseClient;

    private List<Publisher> getPublisherData() {
        return Arrays.asList(new Publisher(null, 1),
                new Publisher(null, 2),
                new Publisher(null, 3));
    }

    private List<Content> getContentData() {
        return Arrays.asList(new Content(null, 1, "This is content!"),
                new Content(null, 1, "Test content"),
                new Content(null, 2, "More test content"));
    }

    @BeforeEach
    public void setup() {
        List<String> statements = Arrays.asList(
                "ALTER TABLE publisher DROP CONSTRAINT CONSTRAINT_63;",
                "DROP TABLE IF EXISTS publisher;",
                "DROP TABLE IF EXISTS content;",
                "CREATE TABLE publisher ( " +
                        "id SERIAL PRIMARY KEY, " +
                        "user_id INT NOT NULL); ",

                "CREATE TABLE content (" +
                        "id SERIAL, " +
                        "publisher_id INT NOT NULL, " +
                        "content VARCHAR(500) NOT NULL," +
                        "PRIMARY KEY (id), " +
                        "FOREIGN KEY (publisher_id) REFERENCES publisher(id));"
        );

        statements.forEach(it -> databaseClient.sql(it)
                .fetch()
                .rowsUpdated()
                .block());

        publisherRepository.deleteAll()
                .thenMany(Flux.fromIterable(getPublisherData()))
                .flatMap(publisherRepository::save)
                .doOnNext(subscriber -> {
                    System.out.println("Publisher inserted from controller test " + subscriber);
                })
                .blockLast();

        contentRepository.deleteAll()
                .thenMany(Flux.fromIterable(getContentData()))
                .flatMap(contentRepository::save)
                .doOnNext(subscribedTo -> {
                    System.out.println("Content inserted from controller test " + subscribedTo);
                })
                .blockLast();
    }

    @Test
    public void getPublisherById() {
        webTestClient.get().uri("pubService/publisher".concat("/{publisherId}"), "1")
                .exchange()
                .expectBody()
                .jsonPath("$.user_id", "1");
    }

    @Test
    public void getPublisherById_NotFound() {
        webTestClient.get().uri("pubService/publisher".concat("/{publisherId}"), "6")
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    public void createPublisher() {
        Publisher publisher =  new Publisher(null, 5);
        webTestClient.post().uri("/pub/publisher/create").contentType(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE))
                .body(Mono.just(publisher), Publisher.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.user_id").isEqualTo("5");
    }

    @Test
    public void createContent() {
        Content content = new Content(null, 1, "Hello this is more content");
        webTestClient.post().uri("/pub/content/create").contentType(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE))
                .body(Mono.just(content), Content.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.publisher_id").isEqualTo("1")
                .jsonPath("$.content").isEqualTo("Hello this is more content");
    }

    @Test
    public void getAllContentByPublisherId(){
        webTestClient.get().uri("/pubService".concat("/content/publisher/{publisherId}"), "1")
                .exchange()
                .expectBody()
                .jsonPath("$.publisher_id", "1");
    }

    @Test
    public void getContentById() {
        webTestClient.get().uri("/pubService".concat("/content/{contentId}"), "1")
                .exchange()
                .expectBody()
                .jsonPath("$.publisher_id", "1");
    }
}