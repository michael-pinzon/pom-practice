package com.globant.pompractice.tests;

import com.globant.pompractice.config.DriverFactory;
import com.globant.pompractice.data.TestData;
import com.globant.pompractice.pages.InventoryPage;
import com.globant.pompractice.pages.LoginPage;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public abstract class BaseTest {

    protected WebDriver driver;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        driver = DriverFactory.createDriver();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    protected InventoryPage loginAsStandardUser() {
        return new LoginPage(driver)
                .open()
                .loginAs(TestData.STANDARD_USERNAME, TestData.PASSWORD);
    }
}
