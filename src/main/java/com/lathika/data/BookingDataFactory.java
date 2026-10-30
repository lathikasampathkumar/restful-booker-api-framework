package com.lathika.data;

import com.lathika.models.Booking;
import com.lathika.utils.TestDataReader;

/**
 * Creates test bookings with unique data for every run.
 * <p>
 * Starts from the valid template in {@code testdata/booking.json} and makes the first
 * name unique ({@code auto_<name>_<nanoTime>}), so parallel tests and reruns never
 * collide. The {@code auto_} prefix makes automation data easy to recognise and clean up.
 * </p>
 * Usage example:
 * <pre>
 * Booking booking = BookingDataFactory.uniqueBooking();   // firstname = "auto_Lathika_83625190245"
 * </pre>
 *
 * @author Lathika Sampathkumar
 */
public class BookingDataFactory {

    /** Marks records created by automation. */
    private static final String PREFIX = "auto_";

    /**
     * Returns a valid booking with a unique first name.
     *
     * @return a new {@link Booking} ready to send
     */
    public static Booking uniqueBooking() {
        Booking booking = TestDataReader.read("booking.json", Booking.class);
        booking.setFirstname(PREFIX + booking.getFirstname() + "_" + System.nanoTime());
        return booking;
    }
}
