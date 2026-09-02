package com.globant.pompractice.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class CartPage extends BasePage {

    private static final By CART_ITEMS = By.cssSelector(".cart_item");

    @FindBy(css = ".title")
    private WebElement pageTitle;

    @FindBy(css = ".cart_item")
    private List<WebElement> cartItems;

    @FindBy(css = ".cart_item button[id^='remove-']")
    private List<WebElement> removeButtons;

    @FindBy(id = "checkout")
    private WebElement checkoutButton;

    public CartPage(WebDriver driver) {
        super(driver);
        wait.until(ExpectedConditions.urlContains("cart"));
        wait.until(ExpectedConditions.visibilityOf(pageTitle));
    }

    public int itemCount() {
        return cartItems.size();
    }

    public CartPage removeAllItems() {
        while (!removeButtons.isEmpty()) {
            int expectedRemainingItems = itemCount() - 1;
            clickWithJavaScript(removeButtons.get(0));
            wait.until(ExpectedConditions.numberOfElementsToBe(CART_ITEMS, expectedRemainingItems));
        }
        return this;
    }

    public boolean isEmpty() {
        return wait.until(driver -> driver.findElements(CART_ITEMS).isEmpty());
    }

    public CheckoutInformationPage checkout() {
        clickWithJavaScript(checkoutButton);
        return new CheckoutInformationPage(driver);
    }
}
