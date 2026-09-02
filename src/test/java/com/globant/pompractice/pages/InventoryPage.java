package com.globant.pompractice.pages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class InventoryPage extends BasePage {

    @FindBy(css = ".title")
    private WebElement pageTitle;

    @FindBy(css = ".inventory_item")
    private List<WebElement> products;

    @FindBy(css = ".inventory_item button")
    private List<WebElement> productActionButtons;

    @FindBy(css = "[data-test='shopping-cart-link']")
    private WebElement shoppingCartLink;

    @FindBy(id = "react-burger-menu-btn")
    private WebElement menuButton;

    @FindBy(id = "logout_sidebar_link")
    private WebElement logoutLink;

    public InventoryPage(WebDriver driver) {
        super(driver);
        wait.until(ExpectedConditions.urlContains("inventory"));
        wait.until(ExpectedConditions.visibilityOf(pageTitle));
    }

    public InventoryPage addRandomProduct() {
        ensureProductsAvailable();
        int randomIndex = ThreadLocalRandom.current().nextInt(productActionButtons.size());
        clickWithJavaScript(productActionButtons.get(randomIndex));
        return this;
    }

    public InventoryPage addProducts(int quantity) {
        ensureValidQuantity(quantity);

        List<Integer> productIndexes = IntStream.range(0, products.size())
                .boxed()
                .collect(Collectors.toCollection(ArrayList::new));
        Collections.shuffle(productIndexes);

        productIndexes.stream()
                .limit(quantity)
                .forEach(index -> clickWithJavaScript(productActionButtons.get(index)));
        return this;
    }

    public CartPage openCart() {
        clickWithJavaScript(shoppingCartLink);
        return new CartPage(driver);
    }

    public LoginPage logout() {
        clickWithJavaScript(menuButton);
        clickWithJavaScript(logoutLink);
        return new LoginPage(driver);
    }

    private void ensureProductsAvailable() {
        if (productActionButtons.isEmpty()) {
            throw new IllegalStateException("No products are available on the inventory page.");
        }
    }

    private void ensureValidQuantity(int quantity) {
        if (quantity < 1 || quantity > products.size()) {
            throw new IllegalArgumentException(
                    "Quantity must be between 1 and " + products.size() + ", but was " + quantity);
        }
    }
}
