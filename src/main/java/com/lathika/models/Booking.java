package com.lathika.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A booking, as sent to and returned by the {@code /booking} endpoints.
 * <p>
 * JSON shape:
 * <pre>
 * {
 *   "firstname": "Jim", "lastname": "Brown", "totalprice": 111, "depositpaid": true,
 *   "bookingdates": { "checkin": "2026-11-01", "checkout": "2026-11-05" },
 *   "additionalneeds": "Breakfast"
 * }
 * </pre>
 * Used both ways: serialized into request bodies (POST, PUT) and deserialized from
 * responses (GET). Wrapper types ({@code Integer}, {@code Boolean}) are used so a field
 * can be {@code null}, which lets tests tell a missing value apart from 0 or false.
 * Unknown JSON fields are ignored so new API fields don't break deserialization;
 * contract changes are caught by the JSON schema test instead.
 * </p>
 *
 * @author Lathika Sampathkumar
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Booking {
    private String firstname;
    private String lastname;
    private Integer totalprice;
    private Boolean depositpaid;
    private BookingDates bookingdates;
    private String additionalneeds;

    /** Required by Jackson to create the object before filling it through setters. */
    public Booking() {}

    /**
     * Builds a complete booking in one line, for request bodies.
     *
     * @param firstname       guest first name
     * @param lastname        guest last name
     * @param totalprice      total price
     * @param depositpaid     whether the deposit is paid
     * @param bookingdates    check-in and check-out dates
     * @param additionalneeds free-text extras, e.g. "Breakfast"
     */
    public Booking(String firstname, String lastname, Integer totalprice,
                   Boolean depositpaid, BookingDates bookingdates, String additionalneeds) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.totalprice = totalprice;
        this.depositpaid = depositpaid;
        this.bookingdates = bookingdates;
        this.additionalneeds = additionalneeds;
    }

    /** @return guest first name */
    public String getFirstname() { return firstname; }
    /** @param firstname guest first name */
    public void setFirstname(String firstname) { this.firstname = firstname; }

    /** @return guest last name */
    public String getLastname() { return lastname; }
    /** @param lastname guest last name */
    public void setLastname(String lastname) { this.lastname = lastname; }

    /** @return total price, or {@code null} if absent */
    public Integer getTotalprice() { return totalprice; }
    /** @param totalprice total price ({@code null} to omit) */
    public void setTotalprice(Integer totalprice) { this.totalprice = totalprice; }

    /** @return whether the deposit is paid, or {@code null} if absent */
    public Boolean getDepositpaid() { return depositpaid; }
    /** @param depositpaid whether the deposit is paid */
    public void setDepositpaid(Boolean depositpaid) { this.depositpaid = depositpaid; }

    /** @return check-in and check-out dates */
    public BookingDates getBookingdates() { return bookingdates; }
    /** @param bookingdates check-in and check-out dates */
    public void setBookingdates(BookingDates bookingdates) { this.bookingdates = bookingdates; }

    /** @return free-text extras */
    public String getAdditionalneeds() { return additionalneeds; }
    /** @param additionalneeds free-text extras */
    public void setAdditionalneeds(String additionalneeds) { this.additionalneeds = additionalneeds; }
}
