package com.lathika.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Response body of {@code POST /booking}.
 * <pre>
 * { "bookingid": 42, "booking": { ...the created booking... } }
 * </pre>
 * Only deserialized from responses, so it needs just the no-arg constructor and setters.
 *
 * @author Lathika Sampathkumar
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateBookingResponse {
    private Integer bookingid;
    private Booking booking;

    /** Required by Jackson for deserialization. */
    public CreateBookingResponse() {}

    /** @return id of the newly created booking */
    public Integer getBookingid() { return bookingid; }
    /** @param bookingid id of the newly created booking */
    public void setBookingid(Integer bookingid) { this.bookingid = bookingid; }

    /** @return the booking as stored by the API */
    public Booking getBooking() { return booking; }
    /** @param booking the booking as stored by the API */
    public void setBooking(Booking booking) { this.booking = booking; }
}
