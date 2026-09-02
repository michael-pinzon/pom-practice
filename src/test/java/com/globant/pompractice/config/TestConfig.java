package com.globant.pompractice.config;

public final class TestConfig {

    private static final String DEFAULT_BASE_URL = "https://www.saucedemo.com/";
    private static final String DEFAULT_BROWSER = "chrome";
    private static final int DEFAULT_DEMO_STEP_DELAY_MS = 480;
    private static final int DEFAULT_DEMO_CURSOR_DURATION_MS = 280;
    private static final int DEFAULT_DEMO_TYPING_DELAY_MS = 25;
    private static final int DEFAULT_DEMO_PAUSE_AFTER_TEST_MS = 1000;
    private static final int DEFAULT_DEMO_FRAME_RATE = 30;

    private TestConfig() {
    }

    public static String baseUrl() {
        return System.getProperty("baseUrl", DEFAULT_BASE_URL);
    }

    public static String browser() {
        return System.getProperty("browser", DEFAULT_BROWSER);
    }

    public static boolean headless() {
        // Demo mode must use a visible browser so an external recorder can see it.
        return !demo() && Boolean.parseBoolean(System.getProperty("headless", "true"));
    }

    public static boolean demo() {
        return Boolean.parseBoolean(System.getProperty("demo", "false"));
    }

    public static String demoRunId() {
        return System.getProperty("demo.runId", "");
    }

    public static int demoStepDelayMs() {
        return intProperty("demo.stepDelayMs", DEFAULT_DEMO_STEP_DELAY_MS, 0);
    }

    public static int demoCursorDurationMs() {
        return intProperty("demo.cursorDurationMs", DEFAULT_DEMO_CURSOR_DURATION_MS, 1);
    }

    public static int demoTypingDelayMs() {
        return intProperty("demo.typingDelayMs", DEFAULT_DEMO_TYPING_DELAY_MS, 0);
    }

    public static int demoPauseAfterTestMs() {
        return intProperty("demo.pauseAfterTestMs", DEFAULT_DEMO_PAUSE_AFTER_TEST_MS, 0);
    }

    public static int demoFrameRate() {
        return intProperty("demo.frameRate", DEFAULT_DEMO_FRAME_RATE, 1);
    }

    public static String demoVideoPath() {
        return System.getProperty("demo.videoPath", "");
    }

    private static int intProperty(String name, int defaultValue, int minimum) {
        String value = System.getProperty(name);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }

        try {
            int parsedValue = Integer.parseInt(value);
            if (parsedValue < minimum) {
                throw new IllegalArgumentException(
                        name + " must be at least " + minimum + ", but was " + parsedValue);
            }
            return parsedValue;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(name + " must be an integer, but was " + value, exception);
        }
    }
}
