package com.globant.pompractice.tests;

import java.lang.reflect.Method;

import com.globant.pompractice.config.DriverFactory;
import com.globant.pompractice.config.DemoVisualizer;
import com.globant.pompractice.config.DemoVideoRecorder;
import com.globant.pompractice.config.TestConfig;
import com.globant.pompractice.data.TestData;
import com.globant.pompractice.listeners.TestProgressListener;
import com.globant.pompractice.pages.InventoryPage;
import com.globant.pompractice.pages.LoginPage;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

@Listeners(TestProgressListener.class)
public abstract class BaseTest {

    protected WebDriver driver;
    private DemoVideoRecorder videoRecorder;

    @BeforeMethod(alwaysRun = true)
    public void setUp(Method testMethod) {
        DemoVisualizer.setTestName(
                testMethod.getDeclaringClass().getSimpleName()
                        + "." + testMethod.getName());
        driver = DriverFactory.createDriver();
        videoRecorder = DemoVideoRecorder.startIfConfigured(driver);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            if (TestConfig.demo()) {
                if (videoRecorder != null) {
                    videoRecorder.captureFor(TestConfig.demoPauseAfterTestMs());
                } else {
                    DemoVisualizer.pause(TestConfig.demoPauseAfterTestMs());
                }
            }
            if (videoRecorder != null) {
                videoRecorder.close();
            }
            driver.quit();
        }
        DemoVisualizer.clearTestName();
    }

    protected InventoryPage loginAsStandardUser() {
        return new LoginPage(driver)
                .open()
                .loginAs(TestData.STANDARD_USERNAME, TestData.PASSWORD);
    }
}
