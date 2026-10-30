package com.lathika.dataproviders;

import java.util.List;

import org.testng.annotations.DataProvider;

import com.lathika.config.ConfigReader;
import com.lathika.models.MissingFieldCase;
import com.lathika.utils.DataReader;

/**
 * TestNG data providers for booking tests.
 * <p>
 * Kept outside the test classes so data and test logic stay separate. Test classes
 * refer to them with {@code dataProviderClass = BookingDataProvider.class}, which is why
 * the methods are {@code static}.
 * </p>
 *
 * @author Lathika Sampathkumar
 */
public class BookingDataProvider {

    /**
     * Supplies one row per "missing required field" case.
     * <p>
     * The file name comes from config, so the same test runs from JSON or Excel:
     * <pre>
     * testdata.missing.fields=missing-fields.json                 (default)
     * mvn test -Dtestdata.missing.fields=missing-fields.xlsx      (Excel)
     * </pre>
     * </p>
     *
     * @return one {@link MissingFieldCase} per row
     */
    @DataProvider(name = "missingFields")
    public static Object[][] missingFields() {
        String file = ConfigReader.get("testdata.missing.fields");
        List<MissingFieldCase> cases = DataReader.readRows(file, MissingFieldCase.class);

        Object[][] data = new Object[cases.size()][1];
        for (int i = 0; i < cases.size(); i++) {
            data[i][0] = cases.get(i);
        }
        return data;
    }
}
