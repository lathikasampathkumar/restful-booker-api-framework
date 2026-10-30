package com.lathika.tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.lathika.base.BaseTest;

import io.restassured.response.Response;

/**
 * Negative test for {@code GET /booking/{id}}.
 *
 * @author Lathika Sampathkumar
 */
public class GetBookingTest extends BaseTest {

    /**
     * Requests a booking id that does not exist and expects 404 Not Found.
     */
    @Test(groups = {"negative", "regression"}, description = "Unknown booking id returns 404")
    public void getNonExistentBookingReturns404() {
        Response response = bookingApi.getBooking(999999999);
        Assert.assertEquals(response.getStatusCode(), 404);
    }
}
