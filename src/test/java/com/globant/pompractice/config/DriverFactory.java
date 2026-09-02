package com.globant.pompractice.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public final class DriverFactory {

    private DriverFactory() {
    }

    public static WebDriver createDriver() {
        if (!"chrome".equalsIgnoreCase(TestConfig.browser())) {
            throw new IllegalArgumentException(
                    "Unsupported browser: " + TestConfig.browser() + ". This exercise is configured for Chrome.");
        }

        ChromeOptions options = new ChromeOptions();
        if (TestConfig.headless()) {
            options.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage");
        }
        options.addArguments("--window-size=1920,1080", "--disable-gpu");

        return new ChromeDriver(options);
    }
}
