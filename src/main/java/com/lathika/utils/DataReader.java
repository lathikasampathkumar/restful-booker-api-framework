package com.lathika.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Format-agnostic reader for row-based test data (data-driven tests).
 * <p>
 * Picks the reader from the file extension:
 * <ul>
 *   <li>{@code .json}: a JSON array of objects</li>
 *   <li>{@code .xlsx}: an Excel sheet with a header row (see {@link ExcelReader})</li>
 * </ul>
 * Either way every row is converted into the requested POJO, so data providers and
 * tests never depend on which format was used.
 * </p>
 * Usage example:
 * <pre>
 * List&lt;MissingFieldCase&gt; rows = DataReader.readRows("missing-fields.xlsx", MissingFieldCase.class);
 * </pre>
 *
 * @author Lathika Sampathkumar
 */
public class DataReader {

    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Reads all rows of a test-data file into POJOs.
     *
     * @param fileName file name inside the testdata folder ({@code .json} or {@code .xlsx})
     * @param type     POJO class for one row
     * @param <T>      the row type
     * @return one object per data row
     * @throws IllegalArgumentException if the extension is not supported
     */
    public static <T> List<T> readRows(String fileName, Class<T> type) {
        List<Map<String, Object>> rows;

        if (fileName.endsWith(".json")) {
            rows = readJsonRows(fileName);
        } else if (fileName.endsWith(".xlsx")) {
            rows = ExcelReader.readRows(fileName);
        } else {
            throw new IllegalArgumentException("Unsupported test data format: " + fileName);
        }

        // Map -> POJO; Jackson also converts "400" text from Excel into an int field
        return rows.stream()
                   .map(row -> mapper.convertValue(row, type))
                   .collect(Collectors.toList());
    }

    /**
     * Reads a JSON array file into a list of key/value maps.
     *
     * @param fileName JSON file name inside the testdata folder
     * @return one map per array element
     */
    private static List<Map<String, Object>> readJsonRows(String fileName) {
        String path = "testdata/" + fileName;
        try (InputStream in = DataReader.class.getClassLoader().getResourceAsStream(path)) {
            if (in == null) {
                throw new IllegalStateException("JSON file not found: " + path);
            }
            return mapper.readValue(in, new TypeReference<List<Map<String, Object>>>() {});
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read JSON file: " + path, e);
        }
    }
}
