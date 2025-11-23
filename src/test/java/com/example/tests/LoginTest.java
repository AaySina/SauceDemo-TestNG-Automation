package com.example.tests;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.Pages.LoginPage;
import org.example.Utils.TestUtils;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;


import java.time.Duration;


public class LoginTest extends BaseTest {

    private static final Logger log = LogManager.getLogger(LoginTest.class);
    // URL target: https://www.saucedemo.com/

    // --- TEST 1: SUCCESS LOGIN (HARD ASSERTION) ---

    @Test(priority = 1, groups = {"smoke"}, description = "Test successful login (Hard Assertions)")
    public void testLoginSuccess() {
        log.info("Starting testLoginSuccess...");

        LoginPage loginPage = new LoginPage(driver);
        String username = config.getProperty("standardUser");
        String password = config.getProperty("password");

        loginPage.login(username, password);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("inventory"));

        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("inventory"),
                "Hard Assert: Gagal navigasi ke halaman inventory setelah login berhasil.");

        log.info("✓ Login successful for user: {}", username);
    }

    // --- TEST 2: FAILED LOGIN (LOCKED OUT USER - HARD ASSERTION) ---

    @Test(priority = 2, description = "Test failed login scenario using HARD ASSERTIONS")
    public void testFailedLoginLockedUser() {
        log.info("Starting testFailedLoginLockedUser...");

        LoginPage loginPage = new LoginPage(driver);
        String username = config.getProperty("failedUser");
        String password = config.getProperty("password");
        String expectedError = config.getProperty("errorMessage");

        loginPage.login(username, password);
        String actualError = loginPage.getFailedLoginErrorMessage();

        log.info("Expected Error: {}", expectedError);
        log.info("Actual Error: {}", actualError);

        Assert.assertEquals(actualError.trim(), expectedError.trim(),
                "Hard Assert: Pesan error tidak cocok untuk locked_out_user.");

        String currentUrl = driver.getCurrentUrl();
        Assert.assertFalse(currentUrl.contains("inventory"),
                "Hard Assert: Seharusnya TIDAK navigasi ke halaman inventory.");

        log.info("✓ HARD ASSERTIONS PASSED for locked_out_user");
    }

    // --- DATA PROVIDER (Membaca Excel) ---

    @DataProvider(name = "loginCredentials")
    public Object[][] loginCredentials() {
        String excelPath = "src/test/resources/data/login-data-test.xlsx";
        String sheetName = "login-tests";

        log.info("Loading test data from Excel: {}", excelPath);
        Object[][] data = TestUtils.getTestData(excelPath, sheetName);
        log.info("Loaded {} test data rows", data.length);

        return data;
    }

    // TEST 3: DATA-DRIVEN LOGIN TEST (SOFT ASSERTIONS)

    @Test(priority = 3, dataProvider = "loginCredentials",
            description = "Data-driven login test using SOFT ASSERTIONS")
    public void testDataDriven(String username, String password, String expectedResult) {

        log.info("=== Starting Data-Driven Test === (User: {}, Expected: {})", username, expectedResult);

        // Menggunakan Soft Assertion (SoftAssert)
        SoftAssert softAssert = new SoftAssert();
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login(username, password);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        // KASUS SUKSES
        if ("success".equalsIgnoreCase(expectedResult)) {
            log.info("Expect SUCCESS navigation.");

            // Soft Assert 1: Cek navigasi URL
            try {
                wait.until(ExpectedConditions.urlContains("inventory"));
                softAssert.assertTrue(driver.getCurrentUrl().contains("inventory"),
                        "Soft Assert 1: URL harus mengandung 'inventory'.");
            } catch (Exception e) {
                softAssert.fail("Soft Assert 1: Gagal navigasi ke halaman inventory.");
            }

            // Soft Assert 2: Cek judul halaman
            String pageTitle = driver.getTitle();
            softAssert.assertTrue(pageTitle.contains("Swag Labs"),
                    "Soft Assert 2: Judul halaman harus mengandung 'Swag Labs'.");

            clearSession();
            driver.get(config.getProperty("baseUrl"));
        }

        // KASUS GAGAL
        else if ("failure".equalsIgnoreCase(expectedResult)) {
            log.info("Expect FAILURE navigation and error message.");
            String actualError = loginPage.getFailedLoginErrorMessage();

            softAssert.assertFalse(actualError.isEmpty(),
                    "Soft Assert 1: Pesan error harus terlihat.");

            softAssert.assertFalse(driver.getCurrentUrl().contains("inventory"),
                    "Soft Assert 2: Seharusnya TIDAK navigasi ke halaman inventory.");
        }
        softAssert.assertAll();
        log.info("=== Data-Driven Test Completed ===\n");
    }

    // TEST 4: FAILED LOGIN (EMPTY PASSWORD - HARD ASSERTION)
    @Test(priority = 4, description = "Test failed login with empty password using HARD ASSERTIONS")
    public void testFailedLoginEmptyPassword() {
        log.info("Starting testFailedLoginEmptyPassword...");

        LoginPage loginPage = new LoginPage(driver);
        String username = config.getProperty("standardUser");
        String emptyPassword = ""; // Password kosong
        String expectedError = "Epic sadface: Password is required"; // Pesan error spesifik Sauce Demo

        loginPage.login(username, emptyPassword);
        String actualError = loginPage.getFailedLoginErrorMessage();

        // Menggunakan Hard Assertion
        Assert.assertEquals(actualError.trim(), expectedError.trim(),
                "Error message must state that password is required.");

        log.info("✓ HARD ASSERTIONS PASSED for empty password scenario.");
    }

    // --- TEST 5: LOGIN PROBLEM USER (HARD ASSERTION) ---
    @Test(priority = 5, description = "Test login with problem_user and check post-login artifacts")
    public void testLoginProblemUser() {
        log.info("Starting testLoginProblemUser...");

        LoginPage loginPage = new LoginPage(driver);
        String username = "problem_user";
        String password = config.getProperty("password");

        loginPage.login(username, password);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("inventory"));

        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("inventory"),
                "Should navigate to inventory page after successful login by problem_user");
        log.info("✓ Login successful for problem_user.");
    }
}