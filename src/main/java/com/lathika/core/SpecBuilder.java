package com.lathika.core;

import static org.hamcrest.Matchers.lessThan;

import com.lathika.config.ConfigReader;
import com.lathika.reporting.ReportLoggingFilter;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

/**
 * Builds the reusable REST Assured specifications used by every API call.
 * <p>
 * A {@link RequestSpecification} holds what every request shares (base URI, headers,
 * timeouts, logging filter). A {@link ResponseSpecification} holds what every successful
 * response must satisfy (status, content type, response-time SLA).
 * </p>
 * <p>
 * A new specification is built on every call instead of sharing one static instance,
 * so tests running in parallel can never leak headers or settings into each other.
 * </p>
 * Usage example:
 * <pre>
 * given().spec(SpecBuilder.requestSpec())
 *     .when().get("/booking/1")
 *     .then().spec(SpecBuilder.okJsonResponse());
 * </pre>
 *
 * @author Lathika Sampathkumar
 */
public class SpecBuilder {

    /**
     * Common request template for all endpoints.
     * <ul>
     *   <li>Base URI from config ({@code base.uri})</li>
     *   <li>{@code Content-Type: application/json} for request bodies</li>
     *   <li>{@code Accept: application/json} (a single value; Restful Booker rejects the
     *       multi-value header REST Assured sends for {@code ContentType.JSON} with 418)</li>
     *   <li>Connect and read timeouts from config</li>
     *   <li>{@link ReportLoggingFilter} so every request and response is logged and reported</li>
     * </ul>
     *
     * @return a new request specification
     */
    public static RequestSpecification requestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(ConfigReader.get("base.uri"))
                .setContentType(ContentType.JSON)
                .setAccept("application/json")
                .setConfig(timeoutConfig())
                .addFilter(new ReportLoggingFilter())
                .build();
    }

    /**
     * Common expectations for a successful JSON response: status 200, JSON content type,
     * and a response time below {@code response.time.sla} (milliseconds) from config.
     *
     * @return a new response specification
     */
    public static ResponseSpecification okJsonResponse() {
        return new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectContentType(ContentType.JSON)
                .expectResponseTime(lessThan((long) ConfigReader.getInt("response.time.sla")))
                .build();
    }

    /**
     * Request template for APIs that use Bearer-token authentication: everything in
     * {@link #requestSpec()} plus an {@code Authorization: Bearer <token>} header.
     * (Restful Booker itself uses a cookie, see {@code BookingApi}.)
     *
     * @param token the access token
     * @return a new request specification with the Authorization header
     */
    public static RequestSpecification authSpec(String token) {
        return new RequestSpecBuilder()
                .addRequestSpecification(requestSpec())
                .addHeader("Authorization", "Bearer " + token)
                .build();
    }

    /**
     * HTTP client timeouts so a slow or hung server cannot block the suite.
     * <ul>
     *   <li>{@code connect.timeout}: max time to open the connection</li>
     *   <li>{@code read.timeout}: max time to wait for the response once connected</li>
     * </ul>
     *
     * @return REST Assured configuration with both timeouts applied
     */
    private static RestAssuredConfig timeoutConfig() {
        return RestAssuredConfig.config().httpClient(
                HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", ConfigReader.getInt("connect.timeout"))
                        .setParam("http.socket.timeout", ConfigReader.getInt("read.timeout")));
    }
}
