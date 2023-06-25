package com.alfonsoristorato.bookmarksproducer.service.api;

import com.alfonsoristorato.bookmarksproducer.service.models.BookmarkBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.equalTo;

public class SaveBookmarkApiTest extends ApiTestConfig {

    @Autowired
    private HttpClient client;

    private final BookmarkBody bookmarkBody = new BookmarkBody(100);

    private static final String accountId = UUID.randomUUID().toString();
    private static final String userId = UUID.randomUUID().toString();

    private static final Map<String, String> validHeaders = new HashMap<>() {
        {
            put("accountId", accountId);
            put("userId", userId);
        }
    };

    private static Stream<Map<String, String>> invalidAndMissingAccountIdHeaders() {
        return Stream.of(
                Map.of(
                        "accountId", "",
                        "userId", ""
                ),
                Map.of("accountId", "",
                        "userId", userId
                ),
                Map.of(
                        "accountId", "not-a-uuid",
                        "userId", userId
                ),
                Map.of(
                        "userId", userId
                )
        );
    }

    private static Stream<Map<String, String>> invalidAndMissingUserIdHeaders() {
        return Stream.of(
                Map.of(
                        "accountId", accountId,
                        "userId", ""
                ),
                Map.of("accountId", accountId,
                        "userId", "not-a-uuid"
                ),
                Map.of(
                        "accountId", accountId
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
    @DisplayName("POST /bookmark/{videoId}:: happy path")
    class saveBookmarkHappyPathTests {

        @Test
        @DisplayName("correct params")
        void saveBookmark_withCorrectParams() {
            client.given()
                    .headers(validHeaders)
                    .body(bookmarkBody)
                    .when()
                    .put("/bookmark/{videoId}", 1)
                    .then()
                    .statusCode(202)
                    .body(blankOrNullString());
        }
    }

    @Nested
    @DisplayName("POST /bookmark/{videoId}:: unhappy path:: headers validation")
    class saveBookmarkUnhappyPathHeaderValidationTests {
        @ParameterizedTest
        @DisplayName("invalid or missing accountId takes precedence over invalid userId")
        @MethodSource("com.alfonsoristorato.bookmarksproducer.service.api.SaveBookmarkApiTest#invalidAndMissingAccountIdHeaders")
        void saveBookmark_withInvalidAccountIdRegardlessOfUserId(Map<String, String> headers) {
            String responseDetails = headers.get("accountId") != null
                    ? "Invalid 'accountId' format provided."
                    : "Required request header 'accountId' missing.";
            client.given()
                    .headers(headers)
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

        @ParameterizedTest
        @DisplayName("invalid or missing userId")
        @MethodSource("com.alfonsoristorato.bookmarksproducer.service.api.SaveBookmarkApiTest#invalidAndMissingUserIdHeaders")
        void saveBookmark_withInvalidUserIdAndValidAccountId(Map<String, String> headers) {
            String responseDetails = headers.get("userId") != null
                    ? "Invalid 'userId' format provided."
                    : "Required request header 'userId' missing.";
            client.given()
                    .headers(headers)
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
    }

    @Nested
    @DisplayName("POST /bookmark/{videoId}:: unhappy path:: request validation")
    class saveBookmarkUnhappyPathRequestValidationTests {

        @ParameterizedTest
        @DisplayName("invalid path variable takes precedence over invalid bookmarkBody")
        @ValueSource(strings = {" ", " 1", "1 ", "aNumber", "two"})
        void saveBookmark_withInvalidPathVariableRegardlessOfBookmarkBody(String pathVariable) {
            client.given()
                    .headers(validHeaders)
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
        @MethodSource("com.alfonsoristorato.bookmarksproducer.service.api.SaveBookmarkApiTest#invalidBookmarkBodyProperties")
        void saveBookmark_withInvalidBookmarkBodyProperties(BookmarkBody bookmarkBody) {
            String responseDetails = bookmarkBody.bookmarkPosition() != null
                    ? "bookmarkPosition needs to be a number."
                    : "bookmarkPosition cannot be null.";
            client.given()
                    .headers(validHeaders)
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
                    .headers(validHeaders)
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
    @DisplayName("{METHOD} /bookmark/{videoId}:: unhappy path:: invalid methods")
    class saveBookmarkUnhappyPathMethodNotSupportedTests {

        @ParameterizedTest
        @DisplayName("invalid methods")
        @ValueSource(strings = {"GET", "PATCH", "DELETE", "POST"})
        void saveBookmark_withInvalidPathVariableRegardlessOfBookmarkBody(String method) {
            client.given()
                    .headers(validHeaders)
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
