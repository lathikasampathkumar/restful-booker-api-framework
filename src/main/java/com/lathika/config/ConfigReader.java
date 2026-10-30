package com.lathika.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Central access point for framework configuration.
 * <p>
 * Values are read from {@code src/main/resources/config.properties}. Any key can be
 * overridden at run time with a JVM system property, which lets CI pipelines switch
 * environments or settings without editing files:
 * <pre>
 * mvn test -Dbase.uri=https://qa.example.com -Dresponse.time.sla=5000
 * </pre>
 * The properties file is loaded once, when the class is first used.
 * </p>
 *
 * @author Lathika Sampathkumar
 */
public class ConfigReader {

    /** Key/value pairs loaded from config.properties. */
    private static final Properties properties = new Properties();

    static {
        try (InputStream in = ConfigReader.class.getClassLoader()
                .getResourceAsStream("config.properties")) {

            if (in == null) {
                throw new IllegalStateException("config.properties not found in classpath");
            }
            properties.load(new InputStreamReader(in, StandardCharsets.UTF_8));

        } catch (IOException e) {
            throw new IllegalStateException("Failed to load config.properties", e);
        }
    }

    /**
     * Returns the value for a configuration key.
     * <p>
     * Lookup order: JVM system property ({@code -Dkey=value}) first, then config.properties.
     * </p>
     *
     * @param key the configuration key, e.g. {@code base.uri}
     * @return the trimmed value
     * @throws IllegalStateException if the key is missing or its value is blank
     */
    public static String get(String key) {
        String value = System.getProperty(key, properties.getProperty(key));

        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Config key missing or empty: " + key);
        }
        return value.trim();
    }

    /**
     * Returns a configuration value as an integer (timeouts, SLAs, token lifetime).
     *
     * @param key the configuration key, e.g. {@code connect.timeout}
     * @return the value parsed as an int
     * @throws NumberFormatException if the value is not a whole number
     */
    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }
}
