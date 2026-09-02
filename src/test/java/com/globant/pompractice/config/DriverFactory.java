package com.globant.pompractice.config;

import java.util.HashMap;
import java.util.Map;

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
        String windowSize = TestConfig.demo() ? "1440,900" : "1920,1080";
        options.addArguments(
                "--window-size=" + windowSize,
                "--disable-gpu",
                "--disable-notifications",
                "--disable-save-password-bubble",
                "--disable-password-generation",
                "--disable-features=PasswordLeakDetection,PasswordCheck,PasswordManagerOnboarding");

        Map<String, Object> preferences = new HashMap<>();
        preferences.put("credentials_enable_service", false);
        preferences.put("profile.password_manager_enabled", false);
        preferences.put("profile.password_manager_leak_detection", false);
        options.setExperimentalOption("prefs", preferences);

        return new ChromeDriver(options);
    }
}
