package com.lathika.tests;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.lathika.base.BaseTest;
import com.lathika.core.SpecBuilder;
import com.lathika.data.BookingDataFactory;
import com.lathika.models.Booking;
import com.lathika.models.CreateBookingResponse;

import io.restassured.response.Response;

/**
 * End-to-end CRUD flow for a booking, kept in one independent test so it never depends
 * on test order.
 *
 * @author Lathika Sampathkumar
 */
public class BookingLifecycleTest extends BaseTest {

    /**
     * Runs the full lifecycle of one booking:
     * <ol>
     *   <li><b>Create</b> a booking with unique data: 200, JSON, within SLA</li>
     *   <li><b>Read</b> it back: same first name</li>
     *   <li><b>Update</b> it with PUT: new total price returned</li>
     *   <li><b>Patch</b> only the last name: last name changes, total price stays
     *       (proves PATCH is partial)</li>
     *   <li><b>Delete</b> it: 201 (Restful Booker's actual code; REST convention is 204)</li>
     *   <li><b>Read</b> again: 404, proving it is really gone</li>
     * </ol>
     */
    @Test(groups = {"e2e", "regression"},
          description = "Booking can be created, read, updated, patched and deleted")
    public void bookingLifecycle() {
        // CREATE
        Booking booking = BookingDataFactory.uniqueBooking();
        Response created = bookingApi.createBooking(booking);
        created.then().spec(SpecBuilder.okJsonResponse());
        int id = created.as(CreateBookingResponse.class).getBookingid();

        // READ
        Booking fetched = bookingApi.getBooking(id).as(Booking.class);
        Assert.assertEquals(fetched.getFirstname(), booking.getFirstname(), "GET after create");

        // UPDATE (PUT replaces the whole booking)
        booking.setTotalprice(2500);
        booking.setAdditionalneeds("Dinner");
        Response updated = bookingApi.updateBooking(id, booking, token);
        updated.then().spec(SpecBuilder.okJsonResponse());
        Assert.assertEquals(updated.as(Booking.class).getTotalprice(), Integer.valueOf(2500), "PUT totalprice");

        // PATCH (only lastname changes, everything else must stay)
        Response patched = bookingApi.partialUpdateBooking(id, Map.of("lastname", "Patched"), token);
        Booking afterPatch = patched.as(Booking.class);
        Assert.assertEquals(afterPatch.getLastname(), "Patched", "PATCH lastname");
        Assert.assertEquals(afterPatch.getTotalprice(), Integer.valueOf(2500), "PATCH must not change totalprice");

        // DELETE
        Assert.assertEquals(bookingApi.deleteBooking(id, token).getStatusCode(), 201, "DELETE status");

        // READ AGAIN -> gone
        Assert.assertEquals(bookingApi.getBooking(id).getStatusCode(), 404, "GET after delete");
    }
}
