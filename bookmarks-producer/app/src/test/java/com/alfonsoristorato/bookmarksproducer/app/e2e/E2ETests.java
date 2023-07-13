package com.alfonsoristorato.bookmarksproducer.app.e2e;

import com.alfonsoristorato.bookmarksproducer.app.BookmarksProducerApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = {BookmarksProducerApplication.class, TestsConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:3333", "port=3333"})
@AutoConfigureWireMock
public abstract class E2ETests {

    static final String WIREMOCK_HEALTH = "health";

    static final String WIREMOCK_VERIFY = "verify";



    @Autowired
    ReactiveKafkaConsumerTemplate<String, String> kafkaConsumerTemplate;

    @Autowired
    ObjectMapper mapper;



}



