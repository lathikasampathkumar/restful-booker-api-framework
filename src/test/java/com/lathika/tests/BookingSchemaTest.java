package com.lathika.tests;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.lathika.base.BaseTest;
import com.lathika.data.BookingDataFactory;
import com.lathika.models.CreateBookingResponse;

/**
 * Contract test for the booking resource.
 * <p>
 * Field-level assertions check values; a schema check verifies the whole structure
 * (required fields, types, date format). It is the safety net for POJOs ignoring
 * unknown fields: if the API removes or renames a field, this test fails.
 * </p>
 *
 * @author Lathika Sampathkumar
 */
public class BookingSchemaTest extends BaseTest {

    /** Id of the booking this test creates, kept for cleanup. */
    private int bookingId;

    /**
     * Creates a booking, fetches it, and validates the GET response against
     * {@code schemas/booking-schema.json}.
     */
    @Test(groups = {"contract", "regression"},
          description = "GET booking response matches the JSON schema")
    public void getBookingMatchesSchema() {
        bookingId = bookingApi.createBooking(BookingDataFactory.uniqueBooking())
                              .as(CreateBookingResponse.class)
                              .getBookingid();

        bookingApi.getBooking(bookingId)
                  .then()
                  .statusCode(200)
                  .body(matchesJsonSchemaInClasspath("schemas/booking-schema.json"));
    }

    /**
     * Deletes the booking created by the test, even if the test failed.
     */
    @AfterMethod(alwaysRun = true)
    public void cleanUp() {
        if (bookingId != 0) bookingApi.deleteBooking(bookingId, token);
    }
}
