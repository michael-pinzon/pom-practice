package com.globant.pompractice.tests;

import com.globant.pompractice.data.TestData;
import com.globant.pompractice.pages.CheckoutCompletePage;

import org.testng.Assert;
import org.testng.annotations.Test;

public class PurchaseTest extends BaseTest {

    private static final String EXPECTED_CONFIRMATION = "Thank you for your order!";

    @Test(description = "Purchase a randomly selected product")
    public void shouldPurchaseRandomProduct() {
        CheckoutCompletePage completePage = loginAsStandardUser()
                .addRandomProduct()
                .openCart()
                .checkout()
                .completeInformation(
                        TestData.FIRST_NAME,
                        TestData.LAST_NAME,
                        TestData.POSTAL_CODE)
                .finishPurchase();

        Assert.assertTrue(
                completePage.isAtCompletePage(),
                "The checkout complete page should be displayed after finishing the purchase.");
        Assert.assertEquals(
                completePage.confirmationMessage(),
                EXPECTED_CONFIRMATION,
                "The purchase confirmation message should be displayed.");
    }
}
