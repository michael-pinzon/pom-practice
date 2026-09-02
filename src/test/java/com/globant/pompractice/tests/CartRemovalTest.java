package com.globant.pompractice.tests;

import com.globant.pompractice.pages.CartPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CartRemovalTest extends BaseTest {

    @Test(description = "Remove three different products from the shopping cart")
    public void shouldRemoveThreeProductsAndLeaveCartEmpty() {
        CartPage cartPage = loginAsStandardUser()
                .addProducts(3)
                .openCart();

        Assert.assertEquals(cartPage.itemCount(), 3, "The cart should contain three products before removal.");

        cartPage.removeAllItems();

        Assert.assertTrue(cartPage.isEmpty(), "The cart should be empty after removing all products.");
    }
}
