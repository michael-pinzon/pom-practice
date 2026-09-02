package com.globant.pompractice.tests;

import com.globant.pompractice.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LogoutTest extends BaseTest {

    @Test(description = "Logout and return to the login page")
    public void shouldLogoutAndReturnToLoginPage() {
        LoginPage loginPage = loginAsStandardUser().logout();

        Assert.assertTrue(loginPage.isAtLoginPage(), "The user should be redirected to the login page.");
    }
}
