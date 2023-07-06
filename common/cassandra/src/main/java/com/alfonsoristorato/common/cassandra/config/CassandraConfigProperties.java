package com.alfonsoristorato.common.cassandra.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Name;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "spring.cassandra")
@Validated
public record CassandraConfigProperties(
        @NotBlank @Name("keyspace-name") String keyspaceName,
        @NotBlank String port,
        @NotBlank @Name("local-datacenter") String localDatacenter,
        @NotBlank @Name("contact-points") String contactPoints,
        @NotBlank @Name("schema-action") String schemaAction) {
}
