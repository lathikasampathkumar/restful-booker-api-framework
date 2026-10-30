package com.lathika.reporting;

import com.aventstack.extentreports.ExtentTest;

/**
 * Holds the report entry of the test currently running on each thread.
 * <p>
 * Stored in a {@link ThreadLocal}, so tests running in parallel each write into their
 * own entry. {@code TestListener} creates the entry when a test starts and removes it
 * when the test ends; {@link ReportLoggingFilter} writes requests and responses into it.
 * </p>
 *
 * @author Lathika Sampathkumar
 */
public class ExtentTestManager {

    /** One report entry per thread. */
    private static final ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();

    /**
     * Creates a report entry for the current thread's test.
     *
     * @param testName name shown in the report
     */
    public static void createTest(String testName) {
        extentTest.set(ReportManager.getExtent().createTest(testName));
    }

    /**
     * @return the current thread's report entry, or {@code null} outside a test
     *         (for example during {@code @BeforeClass})
     */
    public static ExtentTest getTest() {
        return extentTest.get();
    }

    /**
     * Clears the current thread's entry so it cannot leak into the next test.
     */
    public static void removeTest() {
        extentTest.remove();
    }
}
