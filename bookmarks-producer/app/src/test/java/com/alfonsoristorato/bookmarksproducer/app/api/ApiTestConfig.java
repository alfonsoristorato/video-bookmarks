package com.alfonsoristorato.bookmarksproducer.app.api;

import com.alfonsoristorato.bookmarksproducer.app.BookmarksProducerApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = BookmarksProducerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:3333", "port=3333"})
@AutoConfigureWireMock
public abstract class ApiTestConfig {

    static final String WIREMOCK_HEALTH = "health";

    static final String WIREMOCK_VERIFY = "verify";
}
