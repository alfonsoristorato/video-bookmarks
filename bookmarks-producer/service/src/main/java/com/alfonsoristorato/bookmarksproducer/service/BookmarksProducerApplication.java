package com.alfonsoristorato.bookmarksproducer.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class BookmarksProducerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookmarksProducerApplication.class, args);
    }

}
