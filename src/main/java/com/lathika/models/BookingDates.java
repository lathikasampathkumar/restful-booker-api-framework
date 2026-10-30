package com.lathika.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The nested {@code "bookingdates"} object inside a {@link Booking}.
 * <pre>
 * { "checkin": "2026-11-01", "checkout": "2026-11-05" }
 * </pre>
 * Dates are kept as {@code yyyy-MM-dd} strings, exactly as the API sends them.
 *
 * @author Lathika Sampathkumar
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingDates {
    private String checkin;
    private String checkout;

    /** Required by Jackson for deserialization. */
    public BookingDates() {}

    /**
     * @param checkin  check-in date, {@code yyyy-MM-dd}
     * @param checkout check-out date, {@code yyyy-MM-dd}
     */
    public BookingDates(String checkin, String checkout) {
        this.checkin = checkin;
        this.checkout = checkout;
    }

    /** @return check-in date, {@code yyyy-MM-dd} */
    public String getCheckin() { return checkin; }
    /** @param checkin check-in date, {@code yyyy-MM-dd} */
    public void setCheckin(String checkin) { this.checkin = checkin; }

    /** @return check-out date, {@code yyyy-MM-dd} */
    public String getCheckout() { return checkout; }
    /** @param checkout check-out date, {@code yyyy-MM-dd} */
    public void setCheckout(String checkout) { this.checkout = checkout; }
}
