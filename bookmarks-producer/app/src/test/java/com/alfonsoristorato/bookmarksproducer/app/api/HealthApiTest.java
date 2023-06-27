package com.alfonsoristorato.bookmarksproducer.app.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.equalTo;

public class HealthApiTest extends ApiTestConfig {

    @Autowired
    private HttpClient client;

    @Nested
    @DisplayName("GET /actuator/health:: happy path")
    class healthApiTests {

        @Test
        @DisplayName("returns UP with expected components")
        void healthApi() {
            client.given()
                    .when()
                    .get("/actuator/health")
                    .then()
                    .statusCode(200)
                    .body(
                            "size()", equalTo(2),
                            "status", equalTo("UP"),
                            "components.size()", equalTo(2),
                            "components.Kafka.size()", equalTo(1),
                            "components.Kafka.status", equalTo("UP"),
                            "components.SignatureVerifier.size()", equalTo(1),
                            "components.SignatureVerifier.status", equalTo("UP"));
        }
    }

    //TODO: unhappy path

}
