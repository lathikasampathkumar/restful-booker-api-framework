package com.lathika.reporting;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.lathika.config.ConfigReader;

/**
 * Owns the single Extent report for the whole test run.
 * <p>
 * {@link #initialize()} is called once when the suite starts and {@link #flush()} once
 * when it ends (both from {@code TestListener}). Every run writes a <b>new</b> file with
 * the run's start time in its name, for example
 * {@code reports/ExtentReport_2026-10-29_14-05-32.html}, so earlier reports are kept
 * and can be compared.
 * </p>
 *
 * @author Lathika Sampathkumar
 */
public class ReportManager {

    /** Folder where all reports are written. */
    private static final String REPORT_DIR = "reports/";

    /** Timestamp format used in the file name (no ':' because Windows file names can't contain it). */
    private static final DateTimeFormatter TIMESTAMP =
            DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    /** The report shared by all tests in the run. */
    private static ExtentReports extent;

    /** Path of this run's report file, set once in {@link #initialize()}. */
    private static String reportPath;

    /**
     * @return the report, used by {@link ExtentTestManager} to create test entries
     */
    public static ExtentReports getExtent() {
        return extent;
    }

    /**
     * @return path of the report file for the current run, e.g. for logging where it was saved
     */
    public static String getReportPath() {
        return reportPath;
    }

    /**
     * Creates a new timestamped report file, sets its title and records the environment
     * under test.
     */
    public static void initialize() {
        reportPath = REPORT_DIR + "ExtentReport_" + LocalDateTime.now().format(TIMESTAMP) + ".html";

        ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
        spark.config().setDocumentTitle("Restful Booker API Report");
        spark.config().setReportName("Restful Booker API Tests");

        extent = new ExtentReports();
        extent.attachReporter(spark);
        extent.setSystemInfo("Base URI", ConfigReader.get("base.uri"));
    }

    /**
     * Writes everything collected so far to this run's HTML file.
     */
    public static void flush() {
        if (extent != null) {
            extent.flush();
        }
    }
}
