package com.alfonsoristorato.common.kafka.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Name;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "downstreams.kafka.topics")
@Validated
public record KafkaTopicConfigProperties(
        @NotBlank @Name("bookmark") String bookmarkTopic,
        @NotBlank @Name("health") String healthTopic) {
}
