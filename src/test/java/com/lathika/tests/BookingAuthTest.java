package com.lathika.tests;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.lathika.base.BaseTest;
import com.lathika.data.BookingDataFactory;
import com.lathika.models.Booking;
import com.lathika.models.CreateBookingResponse;

import io.restassured.response.Response;

/**
 * Negative authentication tests for protected booking operations.
 * <p>
 * <b>Known defect:</b> Restful Booker returns 403 Forbidden for a missing or invalid
 * token. REST convention is 401 Unauthorized (403 is for a valid user without
 * permission). These tests assert 401 and are in the {@code known-defects} group.
 * </p>
 *
 * @author Lathika Sampathkumar
 */
public class BookingAuthTest extends BaseTest {

    /** Booking used as the update body. */
    private Booking booking;

    /** Id of the booking created for each test. */
    private int bookingId;

    /**
     * Creates a fresh booking before each test, so every test has its own data.
     */
    @BeforeMethod(alwaysRun = true)
    public void createBooking() {
        booking = BookingDataFactory.uniqueBooking();
        bookingId = bookingApi.createBooking(booking).as(CreateBookingResponse.class).getBookingid();
    }

    /**
     * Deletes the booking with a valid token, even if the test failed.
     */
    @AfterMethod(alwaysRun = true)
    public void cleanUp() {
        if (bookingId != 0) bookingApi.deleteBooking(bookingId, token);
    }

    /**
     * Updates a booking with an empty token and expects 401 Unauthorized.
     */
    @Test(groups = {"negative", "known-defects"}, description = "Update without token returns 401")
    public void updateWithoutTokenReturns401() {
        Response response = bookingApi.updateBooking(bookingId, booking, "");
        Assert.assertEquals(response.getStatusCode(), 401, "Missing token should be 401");
    }

    /**
     * Updates a booking with a made-up token and expects 401 Unauthorized.
     */
    @Test(groups = {"negative", "known-defects"}, description = "Update with invalid token returns 401")
    public void updateWithInvalidTokenReturns401() {
        Response response = bookingApi.updateBooking(bookingId, booking, "invalid-token-123");
        Assert.assertEquals(response.getStatusCode(), 401, "Invalid token should be 401");
    }
}
