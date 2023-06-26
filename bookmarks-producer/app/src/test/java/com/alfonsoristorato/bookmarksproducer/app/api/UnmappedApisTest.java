package com.alfonsoristorato.bookmarksproducer.app.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.equalTo;

public class UnmappedApisTest extends ApiTestConfig {

    @Autowired
    private HttpClient client;

    private static Stream<Arguments> unmappedApis() {
        String[] methods = new String[]{"GET", "DELETE", "POST", "PATCH", "PUT"};
        String[] urls = new String[]{"/notMapped", "/", "/bookmark/", "/ bookmark/1"};

        return Arrays.stream(urls)
                .flatMap(url -> Arrays.stream(methods).map(method -> Arguments.of(url, method)))
                .toList()
                .stream();
    }

    @Nested
    @DisplayName("{METHOD} /notMapped")
    class unmappedApisTests {

        @ParameterizedTest
        @MethodSource("com.alfonsoristorato.bookmarksproducer.app.api.UnmappedApisTest#unmappedApis")
        @DisplayName("unmapped apis calls")
        void unmappedApis(String unmappedApi, String method) {
            String encodedUnmappedApi = UriUtils.encodePath(unmappedApi, StandardCharsets.UTF_8);
            client.given()
                    .when()
                    .request(method, unmappedApi)
                    .then()
                    .statusCode(404)
                    .body(
                            "size()", equalTo(2),
                            "description", equalTo("Bad Request Error"),
                            "details", equalTo(String.format("No endpoint %s %s.", method, encodedUnmappedApi)));
        }
    }

}
