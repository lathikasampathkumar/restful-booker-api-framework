package com.lathika.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 * Reads Excel test data with Apache POI.
 * <p>
 * Expects the first sheet of {@code testdata/<fileName>} to have column headers in row 0;
 * every following row becomes one {@code {header: value}} map:
 * <pre>
 * | field     | expectedStatus |      →   { "field": "firstname", "expectedStatus": "400" }
 * | firstname | 400            |
 * </pre>
 * All cells are read as displayed text via {@link DataFormatter}; conversion to numbers
 * happens later when {@link DataReader} maps the row to a POJO.
 * </p>
 *
 * @author Lathika Sampathkumar
 */
public class ExcelReader {

    /**
     * Reads every data row of the first sheet.
     *
     * @param fileName Excel file name inside the testdata folder, e.g. {@code missing-fields.xlsx}
     * @return one map per data row (empty rows are skipped)
     * @throws IllegalStateException if the file or the header row is missing
     */
    public static List<Map<String, Object>> readRows(String fileName) {
        String path = "testdata/" + fileName;

        try (InputStream in = ExcelReader.class.getClassLoader().getResourceAsStream(path)) {
            if (in == null) {
                throw new IllegalStateException("Excel file not found: " + path);
            }

            try (Workbook workbook = WorkbookFactory.create(in)) {
                Sheet sheet = workbook.getSheetAt(0);
                DataFormatter formatter = new DataFormatter();

                Row header = sheet.getRow(0);
                if (header == null) {
                    throw new IllegalStateException("Header row missing in " + path);
                }

                List<Map<String, Object>> rows = new ArrayList<>();
                for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                    Row row = sheet.getRow(r);
                    if (row == null) continue;

                    Map<String, Object> values = new LinkedHashMap<>();
                    for (int c = 0; c < header.getLastCellNum(); c++) {
                        String key = formatter.formatCellValue(header.getCell(c)).trim();
                        Cell cell = row.getCell(c);
                        values.put(key, cell == null ? "" : formatter.formatCellValue(cell).trim());
                    }
                    rows.add(values);
                }
                return rows;
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read Excel file: " + path, e);
        }
    }
}
