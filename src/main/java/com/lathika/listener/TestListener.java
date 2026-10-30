package com.lathika.listener;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.lathika.reporting.ExtentTestManager;
import com.lathika.reporting.ReportManager;

/**
 * TestNG listener that connects test results to the report and the log.
 * <ul>
 *   <li>{@link ISuiteListener}: creates the report when the suite starts and writes it
 *       when the suite finishes</li>
 *   <li>{@link ITestListener}: one report entry per test, marked pass, fail or skip,
 *       and a log line for each</li>
 * </ul>
 * There are no screenshots because this is an API framework; request and response
 * details come from {@code ReportLoggingFilter}. Registered on {@code BaseTest} with
 * {@code @Listeners}.
 *
 * @author Lathika Sampathkumar
 */
public class TestListener implements ITestListener, ISuiteListener {

    private static final Logger log = LogManager.getLogger(TestListener.class);

    /**
     * Creates the Extent report once, before any test runs.
     *
     * @param suite the starting suite
     */
    @Override
    public void onStart(ISuite suite) {
        ReportManager.initialize();
    }

    /**
     * Writes the Extent report to disk after all tests finish.
     *
     * @param suite the finished suite
     */
    @Override
    public void onFinish(ISuite suite) {
        ReportManager.flush();
    }

    /**
     * Creates the report entry for the test that is starting.
     *
     * @param result details of the test
     */
    @Override
    public void onTestStart(ITestResult result) {
        log.info("START  {}", testName(result));
        ExtentTestManager.createTest(testName(result));
    }

    /**
     * Marks the test as passed and clears the thread's entry.
     *
     * @param result details of the test
     */
    @Override
    public void onTestSuccess(ITestResult result) {
        try {
            log.info("PASS   {}", testName(result));
            ExtentTestManager.getTest().pass("Test passed");
        } finally {
            ExtentTestManager.removeTest();
        }
    }

    /**
     * Marks the test as failed with its exception (assertion message and stack trace)
     * and clears the thread's entry.
     *
     * @param result details of the test, including the failure cause
     */
    @Override
    public void onTestFailure(ITestResult result) {
        try {
            log.error("FAIL   {} -> {}", testName(result), String.valueOf(result.getThrowable()));
            ExtentTestManager.getTest().fail(result.getThrowable());
        } finally {
            ExtentTestManager.removeTest();
        }
    }

    /**
     * Marks the test as skipped (for example when a setup method failed). Skipped tests
     * may never have started, so an entry is created first if needed.
     *
     * @param result details of the test
     */
    @Override
    public void onTestSkipped(ITestResult result) {
        if (ExtentTestManager.getTest() == null) {
            ExtentTestManager.createTest(testName(result));
        }
        try {
            ExtentTestManager.getTest().skip(result.getThrowable() != null
                    ? result.getThrowable().getMessage() : "Test skipped");
        } finally {
            ExtentTestManager.removeTest();
        }
    }

    /**
     * Builds the name shown in the report: the method name plus the DataProvider row,
     * e.g. {@code createBookingWithMissingRequiredField [missing firstname]}.
     *
     * @param result details of the test
     * @return display name
     */
    private String testName(ITestResult result) {
        String name = result.getMethod().getMethodName();
        Object[] params = result.getParameters();
        return params.length == 0 ? name : name + " " + Arrays.toString(params);
    }
}
