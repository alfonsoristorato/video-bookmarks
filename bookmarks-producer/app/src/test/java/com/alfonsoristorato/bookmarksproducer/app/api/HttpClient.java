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

import java.io.IOException;

@Component
public class HttpClient {
    @Value("${app.url}")
    private String baseUri;

    @Value("${wiremock.server.port}")
    private String wiremockBaseUri;

    private final ObjectMapper mapper = new ObjectMapper();

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

    public void changeWiremockMapping(String mappingName) throws IOException {
        //TODO: make this dynamic in future
        JsonNode jsonNode = mapper.readValue(ResourceUtils.getFile("src/test/resources/changedMappings/signatureVerifierVerify500.json"), JsonNode.class);

        String mappingId = "5d38f7d3-c69b-40d5-a953-3e705199df3f";

        wiremockClient()
                .body(jsonNode)
                .put("__admin/mappings/{mappingId}", mappingId);
    }
}
