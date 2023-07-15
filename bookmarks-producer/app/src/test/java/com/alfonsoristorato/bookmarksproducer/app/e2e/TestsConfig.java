package com.alfonsoristorato.bookmarksproducer.app.e2e;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

@TestConfiguration
public class TestsConfig {
    @Bean("fixedClock")
    @Primary
    public Clock clock() {
        //Timestamp in milliseconds: 1662026400000
        return Clock.fixed(Instant.parse("2023-12-22T10:15:30.100Z"), ZoneId.of("UTC"));
    }
}

