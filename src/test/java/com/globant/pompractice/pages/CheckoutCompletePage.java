package com.globant.pompractice.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class CheckoutCompletePage extends BasePage {

    @FindBy(css = ".title")
    private WebElement pageTitle;

    @FindBy(css = ".complete-header")
    private WebElement confirmationMessage;

    public CheckoutCompletePage(WebDriver driver) {
        super(driver);
        wait.until(ExpectedConditions.urlContains("checkout-complete"));
        wait.until(ExpectedConditions.visibilityOf(pageTitle));
    }

    public boolean isAtCompletePage() {
        return urlContains("checkout-complete") && isVisible(confirmationMessage);
    }

    public String confirmationMessage() {
        return readText(confirmationMessage);
    }
}
