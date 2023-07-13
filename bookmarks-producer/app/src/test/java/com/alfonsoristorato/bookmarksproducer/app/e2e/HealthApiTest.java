package com.alfonsoristorato.bookmarksproducer.app.e2e;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.equalTo;

public class HealthApiTest extends E2ETests {

    @Autowired
    private HttpClient client;

    @Nested
    @DisplayName("GET /actuator/health:: happy path")
    class healthApiTests {

        @Test
        @DisplayName("returns UP and 200 with expected components")
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

    @Nested
    @DisplayName("GET /actuator/health:: unhappy path:: components down")
    class healthApiTestsUnhappyPathComponentsDown {

        @Test
        @DisplayName("returns DOWN and 503 when SignatureVerifier is DOWN")
        void signatureVerifierDown() {
            client.changeWiremockMapping(WIREMOCK_HEALTH,500);

            client.given()
                    .when()
                    .get("/actuator/health")
                    .then()
                    .statusCode(503)
                    .body(
                            "size()", equalTo(2),
                            "status", equalTo("DOWN"),
                            "components.size()", equalTo(2),
                            "components.Kafka.size()", equalTo(1),
                            "components.Kafka.status", equalTo("UP"),
                            "components.SignatureVerifier.size()", equalTo(1),
                            "components.SignatureVerifier.status", equalTo("DOWN"));
        }

        //TODO: kafka down
    }



}
