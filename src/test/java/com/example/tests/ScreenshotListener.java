package com.example.tests;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenshotListener implements ITestListener {

    private static final String SCREENSHOT_DIR = "screenshots/";

    private String timestamp() {
        return new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        saveScreenshot(result, "FAIL");
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        saveScreenshot(result, "SUCCESS");
    }

    private void saveScreenshot(ITestResult result, String status) {
        try {
            // Ambil instance test yg menjalankan test-nya
            Object testInstance = result.getInstance();

            WebDriver driver = null;

            // Pastikan test instance adalah BaseTest (PENTING)
            if (testInstance instanceof BaseTest) {
                driver = ((BaseTest) testInstance).driver;
            }

            if (driver == null) {
                System.out.println("[ScreenshotListener] WebDriver is null. Screenshot skipped.");
                return;
            }

            // Create folder screenshot jika belum ada
            Files.createDirectories(Paths.get(SCREENSHOT_DIR));

            String filename = result.getMethod().getMethodName()
                    + "_" + status + "_" + timestamp() + ".png";

            String filePath = SCREENSHOT_DIR + filename;

            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(src.toPath(), Paths.get(filePath));

            System.out.println("[ScreenshotListener] Screenshot saved: " + filePath);

        } catch (Exception e) {
            System.out.println("[ScreenshotListener] Failed to save screenshot: " + e.getMessage());
        }
    }
}
