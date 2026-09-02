package com.globant.pompractice.config;

public final class TestConfig {

    private static final String DEFAULT_BASE_URL = "https://www.saucedemo.com/";
    private static final String DEFAULT_BROWSER = "chrome";

    private TestConfig() {
    }

    public static String baseUrl() {
        return System.getProperty("baseUrl", DEFAULT_BASE_URL);
    }

    public static String browser() {
        return System.getProperty("browser", DEFAULT_BROWSER);
    }

    public static boolean headless() {
        return Boolean.parseBoolean(System.getProperty("headless", "true"));
    }
}
