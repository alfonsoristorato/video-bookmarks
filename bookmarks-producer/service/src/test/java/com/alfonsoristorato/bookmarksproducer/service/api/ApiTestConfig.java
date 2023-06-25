package com.alfonsoristorato.bookmarksproducer.service.api;

import com.alfonsoristorato.bookmarksproducer.service.BookmarksProducerApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = BookmarksProducerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:3333", "port=3333" })
public abstract class ApiTestConfig {
}
