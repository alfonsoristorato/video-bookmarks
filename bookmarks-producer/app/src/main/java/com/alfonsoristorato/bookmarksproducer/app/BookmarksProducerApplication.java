package com.alfonsoristorato.bookmarksproducer.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication(scanBasePackages = {
        "com.alfonsoristorato.bookmarksproducer.app",
        "com.alfonsoristorato.common.signatureverifier",
        "com.alfonsoristorato.common.kafka",
        "com.alfonsoristorato.common.utils"
})
@ConfigurationPropertiesScan(basePackages = {
        "com.alfonsoristorato.bookmarksproducer.app",
        "com.alfonsoristorato.common.signatureverifier",
        "com.alfonsoristorato.common.kafka",
})
public class BookmarksProducerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookmarksProducerApplication.class, args);
    }

}
