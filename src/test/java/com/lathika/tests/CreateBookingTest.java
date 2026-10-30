package com.lathika.tests;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.lathika.base.BaseTest;
import com.lathika.models.Booking;
import com.lathika.models.CreateBookingResponse;
import com.lathika.utils.TestDataReader;

import io.restassured.response.Response;

/**
 * Positive test for {@code POST /booking}.
 *
 * @author Lathika Sampathkumar
 */
public class CreateBookingTest extends BaseTest {

    /** Id of the booking this test creates, kept for cleanup. */
    private int bookingId;

    /**
     * Creates a booking with valid data from {@code testdata/booking.json}.
     * <p>
     * Checks:
     * <ul>
     *   <li>status is 200</li>
     *   <li>a booking id is returned</li>
     *   <li>first name and total price in the response match what was sent</li>
     * </ul>
     * </p>
     */
    @Test(groups = {"smoke", "regression"}, description = "Create a booking with valid data")
    public void createBookingWithValidData() {
        // Arrange: build the request body from test data
        Booking booking = TestDataReader.read("booking.json", Booking.class);

        // Act: call the API
        Response response = bookingApi.createBooking(booking);

        // Assert: status, then body
        Assert.assertEquals(response.getStatusCode(), 200);

        CreateBookingResponse body = response.as(CreateBookingResponse.class);
        Assert.assertNotNull(body.getBookingid());
        Assert.assertEquals(body.getBooking().getFirstname(), booking.getFirstname());
        Assert.assertEquals(body.getBooking().getTotalprice(), booking.getTotalprice());

        bookingId = body.getBookingid();
    }

    /**
     * Deletes the booking created by the test, even if the test failed.
     */
    @AfterMethod(alwaysRun = true)
    public void cleanUp() {
        if (bookingId != 0) bookingApi.deleteBooking(bookingId, token);
    }
}
