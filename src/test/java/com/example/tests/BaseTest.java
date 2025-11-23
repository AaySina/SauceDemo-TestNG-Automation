package com.example.tests;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.Core.DriverManager;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;

import java.io.FileInputStream;
import java.util.Properties;

public class BaseTest {

    private static final Logger log = LogManager.getLogger(BaseTest.class);
    protected static Properties config;
    public WebDriver driver;

    /**
     * Load config sekali untuk semua test
     */
    @BeforeSuite(alwaysRun = true)
    public void loadConfig() {
        String env = System.getProperty("env");
        env = (env == null || env.isEmpty()) ? "staging" : env;

        String configFile = "src/test/resources/" + env + ".properties";
        config = new Properties();

        try (FileInputStream fis = new FileInputStream(configFile)) {
            config.load(fis);
            log.info("Loaded config from: {}", configFile);
        } catch (Exception e) {
            log.error("Failed to load config file: {}", configFile, e);
            throw new RuntimeException("Cannot load config file: " + configFile);
        }
    }

    /**
     * Setup browser sebelum setiap test method
     */
    @BeforeMethod(alwaysRun = true)
    @Parameters("browser")
    public void setUp(@Optional("chrome") String browser) {
        log.info("Setting up browser: {}", browser);

        DriverManager.initDriver(browser);
        driver = DriverManager.getDriver();
        driver.manage().window().maximize();

        String url = config.getProperty("baseUrl");
        driver.get(url);
        log.info("Navigated to: {}", url);
    }

    /**
     * Close browser setelah setiap test method
     */
    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            try {
                log.info("Clearing session and cookies...");
                driver.manage().deleteAllCookies();
            } catch (Exception e) {
                log.warn("Could not clear cookies: {}", e.getMessage());
            }
        }

        DriverManager.quitDriver();
        log.info("Browser closed successfully");
    }

    /**
     * Optional manual session clear
     */
    protected void clearSession() {
        if (driver != null) {
            log.info("Manual session clear...");
            driver.manage().deleteAllCookies();
            driver.navigate().refresh();
            log.info("Session cleared and refreshed");
        }
    }
}
