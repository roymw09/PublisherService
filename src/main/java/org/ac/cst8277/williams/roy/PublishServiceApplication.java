package org.ac.cst8277.williams.roy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@SpringBootApplication
public class PublishServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PublishServiceApplication.class, args);
    }

}
