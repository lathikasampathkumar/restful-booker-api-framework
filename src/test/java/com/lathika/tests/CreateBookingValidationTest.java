package com.lathika.tests;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.lathika.base.BaseTest;
import com.lathika.dataproviders.BookingDataProvider;
import com.lathika.models.MissingFieldCase;
import com.lathika.utils.TestDataReader;

import io.restassured.response.Response;

/**
 * Negative, data-driven tests for {@code POST /booking} input validation.
 * <p>
 * <b>Known defect:</b> Restful Booker returns 500 or 200 when a required field is
 * missing, instead of 400. These tests assert the correct behaviour (400) and are in
 * the {@code known-defects} group, which the default suite excludes.
 * </p>
 *
 * @author Lathika Sampathkumar
 */
public class CreateBookingValidationTest extends BaseTest {

    /**
     * Sends a valid booking with one required field removed and expects it to be rejected.
     * <p>
     * Runs once per row of the {@code missingFields} data provider (JSON or Excel).
     * The booking is read as a {@code Map} so the field can be removed completely,
     * which is a truer "missing field" test than sending {@code null}.
     * </p>
     *
     * @param testCase the field to remove and the expected status
     */
    @SuppressWarnings("unchecked")
    @Test(groups = {"negative", "known-defects"},
          dataProvider = "missingFields", dataProviderClass = BookingDataProvider.class,
          description = "Missing required field is rejected with 400")
    public void createBookingWithMissingRequiredField(MissingFieldCase testCase) {
        Map<String, Object> body = TestDataReader.read("booking.json", Map.class);
        body.remove(testCase.getField());

        Response response = bookingApi.createBooking(body);

        Assert.assertEquals(response.getStatusCode(), testCase.getExpectedStatus(),
                "Request with " + testCase + " should be rejected");
    }
}
