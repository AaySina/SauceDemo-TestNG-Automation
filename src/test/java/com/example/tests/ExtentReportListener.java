package com.example.tests;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.openqa.selenium.*;
import org.testng.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExtentReportListener implements ITestListener {

    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();

    private static final String REPORT_DIR = "test-reports/";
    private static final String SCREENSHOT_SUB_DIR = "screenshots/";
    private static final String FULL_SCREENSHOT_DIR = REPORT_DIR + SCREENSHOT_SUB_DIR;

    private static String timestamp() {
        return new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    }

    @Override
    public void onStart(ITestContext context) {
        try {
            Files.createDirectories(Paths.get(REPORT_DIR));
            Files.createDirectories(Paths.get(FULL_SCREENSHOT_DIR));
        } catch (Exception e) {
            System.err.println("Failed to create report directories: " + e.getMessage());
        }

        String reportPath = REPORT_DIR + "ExecutionReport_" + timestamp() + ".html";

        ExtentSparkReporter reporter = new ExtentSparkReporter(reportPath);
        reporter.config().setTheme(Theme.DARK);
        reporter.config().setReportName("Automation Execution Report");
        reporter.config().setDocumentTitle("Test Execution Results");
        reporter.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss");

        extent = new ExtentReports();
        extent.attachReporter(reporter);

        // System info
        extent.setSystemInfo("User", System.getProperty("user.name"));
        extent.setSystemInfo("OS", System.getProperty("os.name"));
        extent.setSystemInfo("Java Version", System.getProperty("java.version"));
        extent.setSystemInfo("Environment", context.getSuite().getParameter("env"));
        extent.setSystemInfo("Browser", context.getSuite().getParameter("browser"));
    }

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String description = result.getMethod().getDescription() != null ?
                result.getMethod().getDescription() : testName;

        ExtentTest test = extent.createTest(testName, description);
        extentTest.set(test);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        extentTest.get().log(Status.PASS, "Test Passed");
        attachScreenshotToReport(result, Status.PASS);
        logExecutionTime(result);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        extentTest.get().log(Status.FAIL, "Test Failed");
        extentTest.get().fail(result.getThrowable());

        attachScreenshotToReport(result, Status.FAIL); // Menggunakan fungsi baru

        logExecutionTime(result);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        extentTest.get().log(Status.SKIP, "Test Skipped");
        if (result.getThrowable() != null) {
            extentTest.get().skip(result.getThrowable());
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        if (extent != null) {
            extent.flush();
        }
    }

    // Helper Methods

    private void logExecutionTime(ITestResult result) {
        long duration = result.getEndMillis() - result.getStartMillis();
        extentTest.get().info("Execution time: " + duration + " ms");
    }

    private void attachScreenshotToReport(ITestResult result, Status status) {
        try {
            Object testInstance = result.getInstance();

            WebDriver driver = null;
            if (testInstance instanceof BaseTest) {
                driver = ((BaseTest) testInstance).driver;
            }

            if (driver == null) {
                extentTest.get().warning("WebDriver is null – screenshot not taken.");
                return;
            }

            String fileName = result.getMethod().getMethodName() + "_" + status.toString().toUpperCase() + "_" + timestamp() + ".png";
            String physicalFilePath = FULL_SCREENSHOT_DIR + fileName;
            String relativePathForReport = SCREENSHOT_SUB_DIR + fileName;

            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            // Menyalin file screenshot
            Files.copy(src.toPath(), Paths.get(physicalFilePath));

            //Menggunakan MediaEntityBuilder dengan pesan yang sesuai status
            if (status == Status.FAIL) {
                extentTest.get().fail("Screenshot Kegagalan:",
                        MediaEntityBuilder.createScreenCaptureFromPath(relativePathForReport).build());
            } else {
                extentTest.get().info("Screenshot Akhir:",
                        MediaEntityBuilder.createScreenCaptureFromPath(relativePathForReport).build());
            }

        } catch (Exception e) {
            extentTest.get().warning("Failed to capture screenshot: " + e.getMessage());
            extentTest.get().fail(e); // Melampirkan error ke laporan
        }
    }
    private void captureScreenshot(ITestResult result) {
    }
}