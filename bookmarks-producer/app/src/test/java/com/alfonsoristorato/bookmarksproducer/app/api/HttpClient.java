package com.alfonsoristorato.bookmarksproducer.app.api;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HttpClient {
    @Value("${app.url}")
    private String baseUri;

    public RequestSpecification given() {
        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        RequestSpecification requestSpecification = requestSpecBuilder
                .setBaseUri(baseUri)
                .setContentType(ContentType.JSON)
                .build();
        return RestAssured.given(requestSpecification);
    }
}
