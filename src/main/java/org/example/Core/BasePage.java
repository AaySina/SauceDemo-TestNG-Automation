package org.example.Core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 Base Page untuk semua Page Object.
 Berisi driver + utility method yang umum digunakan
 */
public class BasePage {

    private static final Logger log = LogManager.getLogger(BasePage.class); // Inisialisasi Logger
    protected WebDriver driver;
    private final WebDriverWait wait;
    private static final Duration TIMEOUT = Duration.ofSeconds(10); // Konstanta Timeout

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, TIMEOUT);
        PageFactory.initElements(driver, this);
        log.debug("BasePage initialized with default timeout: {}s", TIMEOUT.getSeconds());
    }

    /*
    UTILITIES / HELPER METHODS
    */

    /** Menunggu hingga element terlihat */
    protected WebElement waitForVisible(WebElement element) {
        log.debug("Waiting for visibility of element: {}", element.toString());
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    /** Menunggu element dapat diklik */
    protected WebElement waitForClickable(WebElement element) {
        log.debug("Waiting for element to be clickable: {}", element.toString());
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    /** Click aman (dengan wait) */
    protected void click(WebElement element) {
        waitForClickable(element).click();
        log.info("Clicked element successfully: {}", element.toString());
    }

    /** Mengisi input text dengan aman */
    protected void type(WebElement element, String text) {
        WebElement el = waitForVisible(element);
        el.clear();
        el.sendKeys(text);
        log.info("Typed text '{}' into element: {}", text, element.toString());
    }

    /** Mendapatkan teks setelah element terlihat */
    protected String getText(WebElement element) {
        String text = waitForVisible(element).getText().trim();
        log.debug("Retrieved text: '{}' from element.", text);
        return text;
    }

    /** Cek apakah element terlihat (tanpa fail jika tidak ada) */
    protected boolean isElementVisible(WebElement element) {
        try {
            waitForVisible(element);
            log.debug("Element is visible: {}", element.toString());
            return true;
        } catch (org.openqa.selenium.TimeoutException | org.openqa.selenium.NoSuchElementException e) {
            log.debug("Element is NOT visible: {}", element.toString());
            return false;
        }
    }


    /*
       Navigasi Halaman
    */

    public void openUrl(String url) {
        driver.get(url);
        log.info("Navigating to URL: {}", url);
    }
}