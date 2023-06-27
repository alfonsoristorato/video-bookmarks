package com.alfonsoristorato.bookmarksproducer.app.api;

import com.alfonsoristorato.bookmarksproducer.app.models.BookmarkBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.equalTo;

public class SaveBookmarkApiTest extends ApiTestConfig {

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
    class saveBookmarkHappyPathTests {

        @Test
        @DisplayName("correct params")
        void saveBookmark_withCorrectParams() {
            client.given()
                    .headers(VALID_HEADERS)
                    .body(BOOKMARK_BODY)
                    .when()
                    .put("/bookmark/{videoId}", 1)
                    .then()
                    .statusCode(202)
                    .body(blankOrNullString());
        }
    }

    @Nested
    @DisplayName("PUT /bookmark/{videoId}:: unhappy path:: headers validation")
    class saveBookmarkUnhappyPathHeaderFormatValidationTests {
        @ParameterizedTest
        @DisplayName("invalid format or missing accountId takes precedence over following invalid headers")
        @MethodSource("com.alfonsoristorato.bookmarksproducer.app.api.SaveBookmarkApiTest#invalidFormatAndMissingAccountIdHeaders")
        void saveBookmark_withInvalidFormatOrMissingAccountIdRegardlessOfFollowingHeaders(Map<String, String> headers) {
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
        @MethodSource("com.alfonsoristorato.bookmarksproducer.app.api.SaveBookmarkApiTest#invalidFormatAndMissingUserIdHeaders")
        void saveBookmark_withInvalidFormatOrMissingUserIdOfFollowingHeaders(Map<String, String> headers) {
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
        @MethodSource("com.alfonsoristorato.bookmarksproducer.app.api.SaveBookmarkApiTest#invalidFormatAndMissingSignatureHeaders")
        void saveBookmark_withInvalidFormatOrMissingSignatureAndValidRemainingHeaders(Map<String, String> headers) {
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
    class saveBookmarkUnhappyPathRequestValidationTests {

        @ParameterizedTest
        @DisplayName("invalid path variable takes precedence over invalid bookmarkBody")
        @ValueSource(strings = {" ", " 1", "1 ", "aNumber", "two"})
        void saveBookmark_withInvalidPathVariableRegardlessOfBookmarkBody(String pathVariable) {
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
        @MethodSource("com.alfonsoristorato.bookmarksproducer.app.api.SaveBookmarkApiTest#invalidBookmarkBodyProperties")
        void saveBookmark_withInvalidBookmarkBodyProperties(BookmarkBody bookmarkBody) {
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
        void saveBookmark_withMissingBookmarkBody() {
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
    class saveBookmarkUnhappyPathSignatureValidationTests {

        @Test
        @DisplayName("invalid signature")
        void saveBookmark_withInvalidSignature() {
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
    class saveBookmarkUnhappyPathSignatureVerifierDownstreamDownTests {

        @Test
        @DisplayName("signatureVerifier downstream down")
        void saveBookmark_signatureVerifierDownstreamDown() throws IOException {
            client.changeWiremockMapping("1");

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
    class saveBookmarkUnhappyPathMethodNotSupportedTests {

        @ParameterizedTest
        @DisplayName("invalid methods")
        @ValueSource(strings = {"GET", "PATCH", "DELETE", "POST"})
        void saveBookmark_withInvalidPathVariableRegardlessOfBookmarkBody(String method) {
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
