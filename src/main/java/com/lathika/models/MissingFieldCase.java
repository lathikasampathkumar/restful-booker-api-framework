package com.lathika.models;

/**
 * One negative-test case for "create booking with a required field missing".
 * <p>
 * Loaded from {@code testdata/missing-fields.json} or {@code missing-fields.xlsx}:
 * <pre>
 * { "field": "firstname", "expectedStatus": 400 }
 * </pre>
 * </p>
 *
 * @author Lathika Sampathkumar
 */
public class MissingFieldCase {
    private String field;
    private int expectedStatus;

    /** Required by Jackson. */
    public MissingFieldCase() {}

    /** @return name of the field to remove from the request */
    public String getField() { return field; }
    /** @param field name of the field to remove from the request */
    public void setField(String field) { this.field = field; }

    /** @return HTTP status the API should return */
    public int getExpectedStatus() { return expectedStatus; }
    /** @param expectedStatus HTTP status the API should return */
    public void setExpectedStatus(int expectedStatus) { this.expectedStatus = expectedStatus; }

    /**
     * Readable name shown in the report next to the test name,
     * e.g. {@code createBookingWithMissingRequiredField [missing firstname]}.
     *
     * @return "missing &lt;field&gt;"
     */
    @Override
    public String toString() { return "missing " + field; }
}
