package com.alfonsoristorato.bookmarkspersister.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.cassandra.repository.config.EnableReactiveCassandraRepositories;

@SpringBootApplication(scanBasePackages = {
        "com.alfonsoristorato.bookmarkspersister.app",
        "com.alfonsoristorato.common.kafka",
        "com.alfonsoristorato.common.cassandra"
})
@ConfigurationPropertiesScan(basePackages = {
        "com.alfonsoristorato.bookmarkspersister.app",
        "com.alfonsoristorato.common.kafka",
        "com.alfonsoristorato.common.cassandra",
})
@EnableReactiveCassandraRepositories(
        basePackages = "com.alfonsoristorato.common.cassandra")
public class BookmarksPersisterApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookmarksPersisterApplication.class, args);
    }
}
