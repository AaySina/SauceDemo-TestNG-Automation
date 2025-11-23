package org.example.Pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.Core.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage extends BasePage {

    private static final Logger log = LogManager.getLogger(LoginPage.class);

    @FindBy(id = "user-name")
    private WebElement usernameInput;

    @FindBy(id = "password")
    private WebElement passwordInput;

    @FindBy(id = "login-button")
    private WebElement loginButton;

    @FindBy(css = "[data-test='error']")
    private WebElement errorAlert;

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void login(String username, String password) {
        log.info("Logging in with username: {}", username);

        if (username != null && !username.isEmpty()) {
            usernameInput.clear();
            usernameInput.sendKeys(username);
            log.info("Username entered: {}", username);
        }

        if (password != null && !password.isEmpty()) {
            passwordInput.clear();
            passwordInput.sendKeys(password);
            log.info("Password entered: {}", password);
        }

        loginButton.click();
        log.info("user clicked button: {}", username);
    }

    public String getFailedLoginErrorMessage() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOf(errorAlert));
            String errorText = errorAlert.getText().trim();
            log.info("Error message displayed: {}", errorText);
            return errorText;
        } catch (Exception e) {
            log.warn("No error message found");
            return "";
        }
    }
}