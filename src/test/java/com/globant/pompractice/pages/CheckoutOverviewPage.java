package com.globant.pompractice.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class CheckoutOverviewPage extends BasePage {

    @FindBy(css = ".title")
    private WebElement pageTitle;

    @FindBy(id = "finish")
    private WebElement finishButton;

    public CheckoutOverviewPage(WebDriver driver) {
        super(driver);
        wait.until(ExpectedConditions.urlContains("checkout-step-two"));
        wait.until(ExpectedConditions.visibilityOf(pageTitle));
    }

    public CheckoutCompletePage finishPurchase() {
        click(finishButton);
        return new CheckoutCompletePage(driver);
    }
}
