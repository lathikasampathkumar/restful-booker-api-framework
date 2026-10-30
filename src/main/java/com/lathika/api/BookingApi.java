package com.lathika.api;

import static io.restassured.RestAssured.given;

import java.util.Map;

import com.lathika.core.SpecBuilder;
import com.lathika.models.Booking;

import io.restassured.response.Response;

/**
 * API client for the booking resource ({@code /booking}).
 * <p>
 * One method per operation. Every method returns the raw {@link Response} without
 * asserting anything, so the same method is reused by positive and negative tests.
 * Update, patch and delete need an auth token, which Restful Booker expects as a
 * {@code Cookie: token=<value>} header.
 * </p>
 * Usage example:
 * <pre>
 * BookingApi api = new BookingApi();
 * int id = api.createBooking(booking).as(CreateBookingResponse.class).getBookingid();
 * Booking fetched = api.getBooking(id).as(Booking.class);
 * api.deleteBooking(id, token);
 * </pre>
 *
 * @author Lathika Sampathkumar
 */
public class BookingApi {

    /** Collection endpoint. */
    private static final String BOOKING    = "/booking";

    /** Single-booking endpoint; {id} is filled by {@code pathParam("id", ...)}. */
    private static final String BOOKING_ID = "/booking/{id}";

    /**
     * Creates a booking ({@code POST /booking}).
     *
     * @param body a {@link Booking} POJO, or a {@code Map} when a negative test needs to
     *             remove a field completely
     * @return the response; on success the body maps to {@code CreateBookingResponse}
     */
    public Response createBooking(Object body) {
        return given().spec(SpecBuilder.requestSpec())
                    .body(body)
               .when().post(BOOKING)
               .then().extract().response();
    }

    /**
     * Fetches one booking by id ({@code GET /booking/{id}}).
     *
     * @param id the booking id
     * @return the response; 200 with the booking, or 404 if it doesn't exist
     */
    public Response getBooking(int id) {
        return given().spec(SpecBuilder.requestSpec())
                    .pathParam("id", id)
               .when().get(BOOKING_ID)
               .then().extract().response();
    }

    /**
     * Lists booking ids filtered by first name ({@code GET /booking?firstname=...}).
     *
     * @param firstname the first name to filter on (query parameter)
     * @return the response with a JSON array of {@code {"bookingid": n}} objects
     */
    public Response getBookingIdsByName(String firstname) {
        return given().spec(SpecBuilder.requestSpec())
                    .queryParam("firstname", firstname)
               .when().get(BOOKING)
               .then().extract().response();
    }

    /**
     * Replaces a whole booking ({@code PUT /booking/{id}}). Fields left out of the
     * body are not kept, which is what distinguishes PUT from PATCH.
     *
     * @param id      the booking id
     * @param booking the complete new booking
     * @param token   auth token (sent as a cookie)
     * @return the response with the updated booking
     */
    public Response updateBooking(int id, Booking booking, String token) {
        return given().spec(SpecBuilder.requestSpec())
                    .cookie("token", token)
                    .pathParam("id", id)
                    .body(booking)
               .when().put(BOOKING_ID)
               .then().extract().response();
    }

    /**
     * Changes only the given fields of a booking ({@code PATCH /booking/{id}}).
     *
     * @param id     the booking id
     * @param fields the fields to change, e.g. {@code Map.of("firstname", "New")}
     * @param token  auth token (sent as a cookie)
     * @return the response with the full booking after the change
     */
    public Response partialUpdateBooking(int id, Map<String, Object> fields, String token) {
        return given().spec(SpecBuilder.requestSpec())
                    .cookie("token", token)
                    .pathParam("id", id)
                    .body(fields)
               .when().patch(BOOKING_ID)
               .then().extract().response();
    }

    /**
     * Deletes a booking ({@code DELETE /booking/{id}}).
     * Restful Booker returns 201 here (REST convention would be 204).
     *
     * @param id    the booking id
     * @param token auth token (sent as a cookie)
     * @return the response
     */
    public Response deleteBooking(int id, String token) {
        return given().spec(SpecBuilder.requestSpec())
                    .cookie("token", token)
                    .pathParam("id", id)
               .when().delete(BOOKING_ID)
               .then().extract().response();
    }
}
