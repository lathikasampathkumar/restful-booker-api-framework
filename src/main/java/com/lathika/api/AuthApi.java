package com.lathika.api;

import static io.restassured.RestAssured.given;

import com.lathika.config.ConfigReader;
import com.lathika.core.SpecBuilder;
import com.lathika.models.AuthRequest;
import com.lathika.models.AuthResponse;

import io.restassured.response.Response;

/**
 * API client for the authentication endpoint ({@code POST /auth}).
 * <p>
 * Plays the role a page object plays in a UI framework: it knows how to call the
 * endpoint, while tests decide what the correct outcome is.
 * </p>
 * Usage example:
 * <pre>
 * String token = new AuthApi().getToken();                                  // valid login
 * Response bad = new AuthApi().createToken(new AuthRequest("admin", "x"));  // negative test
 * </pre>
 *
 * @author Lathika Sampathkumar
 */
public class AuthApi {

    /**
     * Sends a login request with the given credentials and returns the raw response.
     * No assertions are made here, so the same method serves positive and negative tests.
     *
     * @param request username and password, serialized to JSON by Jackson
     * @return the full response (status, headers, body)
     */
    public Response createToken(AuthRequest request) {
        return given()
                    .spec(SpecBuilder.requestSpec())
                    .body(request)
               .when()
                    .post("/auth")
               .then()
                    .extract().response();
    }

    /**
     * Logs in with the credentials from config ({@code auth.username}, {@code auth.password})
     * and returns only the token string.
     * <p>
     * Flow: config → {@link AuthRequest} POJO → JSON → {@code POST /auth} → JSON →
     * {@link AuthResponse} POJO → token.
     * </p>
     *
     * @return the token, or {@code null} if the API rejected the credentials
     */
    public String getToken() {
        AuthRequest request = new AuthRequest(
                ConfigReader.get("auth.username"),
                ConfigReader.get("auth.password"));

        return createToken(request)
                .as(AuthResponse.class)
                .getToken();
    }
}
