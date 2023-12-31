package com.alfonsoristorato.bookmarksproducer.app.e2e;

import com.alfonsoristorato.bookmarksproducer.app.models.BookmarkBody;
import com.alfonsoristorato.common.utils.models.BookmarkMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static reactor.test.StepVerifier.DEFAULT_VERIFY_TIMEOUT;

public class ProduceBookmarkApiTest extends E2ETests {

    @Autowired
    private HttpClient client;

    private static final BookmarkBody BOOKMARK_BODY = new BookmarkBody(100);

    private static final String ACCOUNT_ID = UUID.randomUUID().toString();

    private static final String USER_ID = UUID.randomUUID().toString();

    private static final String SIGNATURE = "validSignature";

    private static final Map<String, String> VALID_HEADERS = new HashMap<>() {
        {
            put("accountId", ACCOUNT_ID);
            put("userId", USER_ID);
            put("signature", SIGNATURE);
        }
    };

    private static Stream<Map<String, String>> invalidFormatAndMissingAccountIdHeaders() {
        return Stream.of(
                Map.of(
                        "accountId", "",
                        "userId", "",
                        "signature", ""
                ),
                Map.of("accountId", "",
                        "userId", USER_ID,
                        "signature", SIGNATURE
                ),
                Map.of(
                        "accountId", "not-a-uuid",
                        "userId", USER_ID,
                        "signature", SIGNATURE
                ),
                Map.of(
                        "userId", USER_ID,
                        "signature", SIGNATURE
                )
        );
    }

    private static Stream<Map<String, String>> invalidFormatAndMissingUserIdHeaders() {
        return Stream.of(
                Map.of(
                        "accountId", ACCOUNT_ID,
                        "userId", "",
                        "signature", ""
                ),
                Map.of("accountId", ACCOUNT_ID,
                        "userId", "not-a-uuid",
                        "signature", SIGNATURE
                ),
                Map.of(
                        "accountId", ACCOUNT_ID,
                        "signature", SIGNATURE
                )
        );
    }

    private static Stream<Map<String, String>> invalidFormatAndMissingSignatureHeaders() {
        return Stream.of(
                Map.of(
                        "accountId", ACCOUNT_ID,
                        "userId", USER_ID,
                        "signature", ""
                ),
                Map.of("accountId", ACCOUNT_ID,
                        "userId", USER_ID,
                        "signature", " "
                ),
                Map.of(
                        "accountId", ACCOUNT_ID,
                        "userId", USER_ID
                )
        );
    }

    private static Stream<BookmarkBody> invalidBookmarkBodyProperties() {
        return Stream.of(
                new BookmarkBody(-1),
                new BookmarkBody(null)
        );
    }

    @Nested
    @DisplayName("PUT /bookmark/{videoId}:: happy path")
    class produceBookmarkHappyPathTests {

        @Test
        @DisplayName("correct params calls writes to Kafka")
        void produceBookmark_withCorrectParams() throws JsonProcessingException {
            client.given()
                    .headers(VALID_HEADERS)
                    .body(BOOKMARK_BODY)
                    .when()
                    .put("/bookmark/{videoId}", 1)
                    .then()
                    .statusCode(202)
                    .body(blankOrNullString());

            BookmarkMessage bookmarkMessage = new BookmarkMessage(
                    VALID_HEADERS.get("accountId"),
                    VALID_HEADERS.get("userId"),
                    1,
                    BOOKMARK_BODY.bookmarkPosition(),
                    Instant.parse("2023-12-22T10:15:30.100Z")
            );
            String bookmarkMessageString = mapper.writeValueAsString(bookmarkMessage);

            StepVerifier.create(kafkaConsumerTemplate.receiveAutoAck())
                    .assertNext(receiverRecord -> assertThat(receiverRecord.value()).isEqualTo(bookmarkMessageString))
                    .thenCancel()
                    .verify(DEFAULT_VERIFY_TIMEOUT);

        }
    }

    @Nested
    @DisplayName("PUT /bookmark/{videoId}:: unhappy path:: headers validation")
    class produceBookmarkUnhappyPathHeaderFormatValidationTests {
        @ParameterizedTest
        @DisplayName("invalid format or missing accountId takes precedence over following invalid headers")
        @MethodSource("com.alfonsoristorato.bookmarksproducer.app.e2e.ProduceBookmarkApiTest#invalidFormatAndMissingAccountIdHeaders")
        void produceBookmark_withInvalidFormatOrMissingAccountIdRegardlessOfFollowingHeaders(Map<String, String> headers) {
            String responseDetails = headers.get("accountId") != null
                    ? "Invalid 'accountId' format provided."
                    : "Required request header 'accountId' missing.";
            client.given()
                    .headers(headers)
                    .body(BOOKMARK_BODY)
                    .when()
                    .put("/bookmark/{videoId}", 1)
                    .then()
                    .statusCode(400)
                    .body(
                            "size()", equalTo(2),
                            "description", equalTo("Bad Request Error"),
                            "details", equalTo(responseDetails)
                    );
        }

        @ParameterizedTest
        @DisplayName("invalid format or missing userId takes precedence over following invalid headers")
        @MethodSource("com.alfonsoristorato.bookmarksproducer.app.e2e.ProduceBookmarkApiTest#invalidFormatAndMissingUserIdHeaders")
        void produceBookmark_withInvalidFormatOrMissingUserIdOfFollowingHeaders(Map<String, String> headers) {
            String responseDetails = headers.get("userId") != null
                    ? "Invalid 'userId' format provided."
                    : "Required request header 'userId' missing.";
            client.given()
                    .headers(headers)
                    .body(BOOKMARK_BODY)
                    .when()
                    .put("/bookmark/{videoId}", 1)
                    .then()
                    .statusCode(400)
                    .body(
                            "size()", equalTo(2),
                            "description", equalTo("Bad Request Error"),
                            "details", equalTo(responseDetails)
                    );
        }

        @ParameterizedTest
        @DisplayName("invalid format or missing signature")
        @MethodSource("com.alfonsoristorato.bookmarksproducer.app.e2e.ProduceBookmarkApiTest#invalidFormatAndMissingSignatureHeaders")
        void produceBookmark_withInvalidFormatOrMissingSignatureAndValidRemainingHeaders(Map<String, String> headers) {
            String responseDetails = headers.get("signature") != null
                    ? "Invalid 'signature' format provided."
                    : "Required request header 'signature' missing.";
            client.given()
                    .headers(headers)
                    .body(BOOKMARK_BODY)
                    .when()
                    .put("/bookmark/{videoId}", 1)
                    .then()
                    .statusCode(400)
                    .body(
                            "size()", equalTo(2),
                            "description", equalTo("Bad Request Error"),
                            "details", equalTo(responseDetails)
                    );
        }
    }

    @Nested
    @DisplayName("POST /bookmark/{videoId}:: unhappy path:: request validation")
    class produceBookmarkUnhappyPathRequestValidationTests {

        @ParameterizedTest
        @DisplayName("invalid path variable takes precedence over invalid bookmarkBody")
        @ValueSource(strings = {" ", " 1", "1 ", "aNumber", "two"})
        void produceBookmark_withInvalidPathVariableRegardlessOfBookmarkBody(String pathVariable) {
            client.given()
                    .headers(VALID_HEADERS)
                    .body(new BookmarkBody(null))
                    .when()
                    .put("/bookmark/{videoId}", pathVariable)
                    .then()
                    .statusCode(400)
                    .body(
                            "size()", equalTo(2),
                            "description", equalTo("Bad Request Error"),
                            "details", equalTo("videoId needs to be a valid number.")
                    );
        }

        @ParameterizedTest
        @DisplayName("invalid bookmarkBody properties")
        @MethodSource("com.alfonsoristorato.bookmarksproducer.app.e2e.ProduceBookmarkApiTest#invalidBookmarkBodyProperties")
        void produceBookmark_withInvalidBookmarkBodyProperties(BookmarkBody bookmarkBody) {
            String responseDetails = bookmarkBody.bookmarkPosition() != null
                    ? "bookmarkPosition needs to be a number."
                    : "bookmarkPosition cannot be null.";
            client.given()
                    .headers(VALID_HEADERS)
                    .body(bookmarkBody)
                    .when()
                    .put("/bookmark/{videoId}", 1)
                    .then()
                    .statusCode(400)
                    .body(
                            "size()", equalTo(2),
                            "description", equalTo("Bad Request Error"),
                            "details", equalTo(responseDetails)
                    );
        }

        @Test
        @DisplayName("missing bookmarkBody")
        void produceBookmark_withMissingBookmarkBody() {
            client.given()
                    .headers(VALID_HEADERS)
                    .when()
                    .put("/bookmark/{videoId}", 1)
                    .then()
                    .statusCode(400)
                    .body(
                            "size()", equalTo(2),
                            "description", equalTo("Bad Request Error"),
                            "details", equalTo("Required request body is missing.")
                    );
        }
    }

    @Nested
    @DisplayName("PUT /bookmark/{videoId}:: unhappy path:: invalid signature")
    class produceBookmarkUnhappyPathSignatureValidationTests {

        @Test
        @DisplayName("invalid signature")
        void produceBookmark_withInvalidSignature() {
            Map<String, String> invalidSignatureHeaders = Map.of(
                    "accountId", ACCOUNT_ID,
                    "userId", USER_ID,
                    "signature", "invalidSignature"
            );
            client.given()
                    .headers(invalidSignatureHeaders)
                    .body(new BookmarkBody(100))
                    .when()
                    .put("/bookmark/{videoId}", 1)
                    .then()
                    .statusCode(400)
                    .body(
                            "size()", equalTo(2),
                            "description", equalTo("Bad Request Error"),
                            "details", equalTo("signature is invalid.")
                    );
        }
    }

    @Nested
    @DisplayName("PUT /bookmark/{videoId}:: unhappy path:: signatureVerifier downstream down")
    class produceBookmarkUnhappyPathSignatureVerifierDownstreamDownTests {

        @Test
        @DisplayName("signatureVerifier downstream down")
        void produceBookmark_signatureVerifierDownstreamDown() {
            client.changeWiremockMapping(WIREMOCK_VERIFY, 500);

            client.given()
                    .headers(VALID_HEADERS)
                    .body(new BookmarkBody(100))
                    .when()
                    .put("/bookmark/{videoId}", 1)
                    .then()
                    .statusCode(500)
                    .body(
                            "size()", equalTo(2),
                            "description", equalTo("Downstream Error"),
                            "details", equalTo("SignatureVerifier is down.")
                    );
        }
    }


    @Nested
    @DisplayName("{METHOD} /bookmark/{videoId}:: unhappy path:: invalid methods")
    class produceBookmarkUnhappyPathMethodNotSupportedTests {

        @ParameterizedTest
        @DisplayName("invalid methods")
        @ValueSource(strings = {"GET", "PATCH", "DELETE", "POST"})
        void produceBookmark_withInvalidPathVariableRegardlessOfBookmarkBody(String method) {
            client.given()
                    .headers(VALID_HEADERS)
                    .body(new BookmarkBody(null))
                    .when()
                    .request(method, "/bookmark/{videoId}", 1)
                    .then()
                    .statusCode(405)
                    .body(
                            "size()", equalTo(2),
                            "description", equalTo("Bad Request Error"),
                            "details", equalTo(String.format("Request method '%s' is not supported", method))
                    );
        }
    }
}
