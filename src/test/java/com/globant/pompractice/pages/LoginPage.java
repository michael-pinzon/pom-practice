package com.globant.pompractice.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.globant.pompractice.config.TestConfig;

public class LoginPage extends BasePage {

    @FindBy(id = "user-name")
    private WebElement usernameField;

    @FindBy(id = "password")
    private WebElement passwordField;

    @FindBy(id = "login-button")
    private WebElement loginButton;

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage open() {
        driver.get(TestConfig.baseUrl());
        wait.until(driver -> driver.getCurrentUrl().equals(TestConfig.baseUrl()));
        wait.until(org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf(usernameField));
        return this;
    }

    public InventoryPage loginAs(String username, String password) {
        type(usernameField, username);
        type(passwordField, password);
        click(loginButton);
        return new InventoryPage(driver);
    }

    public boolean isDisplayed() {
        return isVisible(usernameField) && isVisible(passwordField) && isVisible(loginButton);
    }

    public boolean isAtLoginPage() {
        return isUrl(TestConfig.baseUrl()) && isDisplayed();
    }
}
