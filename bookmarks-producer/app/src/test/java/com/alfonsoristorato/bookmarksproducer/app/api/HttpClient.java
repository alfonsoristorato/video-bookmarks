package com.alfonsoristorato.bookmarksproducer.app.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import wiremock.org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

@Component
public class HttpClient {
    @Value("${app.url}")
    private String baseUri;

    @Value("${wiremock.server.port}")
    private String wiremockBaseUri;

    private final ObjectMapper mapper = new ObjectMapper();

    private final static Map<String, String> WIREMOCK_STUBS = Map.of(
            "health", "05ee4600-b8bf-4ae8-a1da-337e561c5e0a",
            "verify", "5d38f7d3-c69b-40d5-a953-3e705199df3f"
    );

    public RequestSpecification given() {
        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        RequestSpecification requestSpecification = requestSpecBuilder
                .setBaseUri(baseUri)
                .setContentType(ContentType.JSON)
                .build();
        return RestAssured.given(requestSpecification);
    }

    private RequestSpecification wiremockClient() {
        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        RequestSpecification requestSpecification = requestSpecBuilder
                .setBaseUri(String.format("http://localhost:%s", wiremockBaseUri))
                .setContentType(ContentType.JSON)
                .build();
        return RestAssured.given(requestSpecification);
    }

    public void changeWiremockMapping(String mappingName, Integer statusCodeToReturn) {
        WiremockMappingDetails wiremockMappingDetails =
                WIREMOCK_STUBS
                        .entrySet()
                        .stream()
                        .filter(entry -> matchesMappingName(entry, mappingName))
                        .map(filteredEntry -> convertEntryToMappingDetails(filteredEntry, statusCodeToReturn))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("no such mapping"));

        wiremockClient()
                .body(wiremockMappingDetails.body)
                .put("__admin/mappings/{mappingId}", wiremockMappingDetails.mappingId);
    }

    private boolean matchesMappingName(Map.Entry<String, String> entry, String mappingName) {
        return entry.getKey().matches(mappingName);
    }

    private File getChangedMapping(String mappingName, Integer statusCodeToReturn) {
        try {
            return ResourceUtils.getFile(String.format("src/test/resources/changedMappings/signatureVerifier%s%s.json", StringUtils.capitalize(mappingName), statusCodeToReturn));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("no such file");
        }
    }

    private String convertFileToString(String mappingName, Integer statusCodeToReturn) {
        try {
            return mapper.readValue(getChangedMapping(mappingName, statusCodeToReturn), JsonNode.class).toString();
        } catch (IOException e) {
            throw new RuntimeException("cannot convert file");
        }
    }

    private WiremockMappingDetails convertEntryToMappingDetails(Map.Entry<String, String> entry, Integer statusCodeToReturn) {
        String body = convertFileToString(entry.getKey(), statusCodeToReturn);
        String mappingId = entry.getValue();
        return new WiremockMappingDetails(body, mappingId);
    }

    private record WiremockMappingDetails(String body, String mappingId) {
    }
}
