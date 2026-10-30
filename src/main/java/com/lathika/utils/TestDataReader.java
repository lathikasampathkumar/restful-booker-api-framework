package com.lathika.utils;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Reads a single JSON test-data file into a POJO (or a {@code Map}).
 * <p>
 * Files live in {@code src/main/resources/testdata/}.
 * </p>
 * Usage example:
 * <pre>
 * Booking booking = TestDataReader.read("booking.json", Booking.class);
 * Map&lt;String, Object&gt; raw = TestDataReader.read("booking.json", Map.class);
 * </pre>
 *
 * @author Lathika Sampathkumar
 */
public class TestDataReader {

    /** Jackson converter, the same library REST Assured uses for {@code .as(...)}. */
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Reads {@code testdata/<fileName>} and converts it to the given type.
     *
     * @param fileName file name inside the testdata folder, e.g. {@code booking.json}
     * @param type     class to convert into, e.g. {@code Booking.class}
     * @param <T>      the returned type
     * @return the file content as an object of {@code type}
     * @throws IllegalStateException if the file is missing or not valid JSON for {@code type}
     */
    public static <T> T read(String fileName, Class<T> type) {
        try (InputStream in = TestDataReader.class.getClassLoader()
                .getResourceAsStream("testdata/" + fileName)) {

            if (in == null) {
                throw new IllegalStateException("Test data file not found: " + fileName);
            }
            return mapper.readValue(in, type);

        } catch (IOException e) {
            throw new IllegalStateException("Failed to read test data: " + fileName, e);
        }
    }
}
