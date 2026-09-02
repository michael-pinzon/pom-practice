package com.globant.pompractice.config;

import java.util.Objects;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Adds a small, non-interactive visual layer to the page while a test is in
 * demo mode. It makes automated actions easy to follow in a recording without
 * changing the application under test.
 */
public final class DemoVisualizer {

    private static final ThreadLocal<String> TEST_NAME = new ThreadLocal<>();

    private static final String SHOW_TITLE_SCRIPT = """
            const browserTitle = arguments[0] || 'POM Demo';
            const visibleTestName = arguments[1] || 'Demo';
            document.title = browserTitle;

            let root = document.getElementById('pom-demo-overlay');
            if (!root) {
                root = document.createElement('div');
                root.id = 'pom-demo-overlay';
                root.innerHTML = `
                    <div data-role="test-title"></div>
                    <div data-role="focus"></div>
                    <div data-role="cursor"></div>
                    <div data-role="label"></div>`;
                root.style.cssText = [
                    'position:fixed',
                    'inset:0',
                    'z-index:2147483647',
                    'pointer-events:none',
                    'font-family:Arial,sans-serif'
                ].join(';');
                document.documentElement.appendChild(root);
            }

            const title = root.querySelector('[data-role="test-title"]');
            title.textContent = 'Prueba: ' + visibleTestName;
            title.style.cssText = [
                'position:absolute',
                'left:18px',
                'top:16px',
                'z-index:30',
                'padding:10px 14px',
                'border-left:4px solid #ff2d55',
                'border-radius:6px',
                'background:rgba(17,24,39,.95)',
                'color:#fff',
                'font-size:14px',
                'font-weight:700',
                'line-height:1.2',
                'box-shadow:0 2px 10px rgba(0,0,0,.30)'
            ].join(';');
            root.style.display = 'block';
            root.style.opacity = '1';
            """;

    private static final String SHOW_ACTION_SCRIPT = """
            const element = arguments[0];
            const action = arguments[1] || 'Acción';
            const duration = Number(arguments[2]) || 450;
            const browserTitle = arguments[3] || document.title;
            const visibleTestName = arguments[4] || 'Demo';

            if (!element || !element.isConnected) {
                return;
            }

            element.scrollIntoView({block: 'center', inline: 'center', behavior: 'auto'});
            document.title = browserTitle;

            let root = document.getElementById('pom-demo-overlay');
            if (!root) {
                root = document.createElement('div');
                root.id = 'pom-demo-overlay';
                root.innerHTML = `
                    <div data-role="test-title"></div>
                    <div data-role="focus"></div>
                    <div data-role="cursor"></div>
                    <div data-role="label"></div>`;
                root.style.cssText = [
                    'position:fixed',
                    'inset:0',
                    'z-index:2147483647',
                    'pointer-events:none',
                    'font-family:Arial,sans-serif'
                ].join(';');
                document.documentElement.appendChild(root);
            }

            const focus = root.querySelector('[data-role="focus"]');
            const cursor = root.querySelector('[data-role="cursor"]');
            const label = root.querySelector('[data-role="label"]');
            const testTitle = root.querySelector('[data-role="test-title"]');
            const rectangle = element.getBoundingClientRect();
            const centerX = rectangle.left + rectangle.width / 2;
            const centerY = rectangle.top + rectangle.height / 2;
            const previous = window.__pomDemoCursorPosition || {x: 36, y: 36};

            testTitle.textContent = 'Prueba: ' + visibleTestName;
            testTitle.style.cssText = [
                'position:absolute',
                'left:18px',
                'top:16px',
                'z-index:30',
                'padding:10px 14px',
                'border-left:4px solid #ff2d55',
                'border-radius:6px',
                'background:rgba(17,24,39,.95)',
                'color:#fff',
                'font-size:14px',
                'font-weight:700',
                'line-height:1.2',
                'box-shadow:0 2px 10px rgba(0,0,0,.30)'
            ].join(';');

            focus.style.cssText = [
                'position:absolute',
                `left:${Math.max(rectangle.left - 10, 4)}px`,
                `top:${Math.max(rectangle.top - 10, 4)}px`,
                `width:${Math.max(rectangle.width + 20, 26)}px`,
                `height:${Math.max(rectangle.height + 20, 26)}px`,
                'border:3px solid #ff2d55',
                'border-radius:10px',
                'z-index:1',
                'box-shadow:0 0 0 9999px rgba(12,18,31,.20), 0 0 18px rgba(255,45,85,.85)',
                'opacity:1',
                'transform:scale(.94)',
                `transition:transform ${Math.min(duration, 700)}ms ease, opacity 180ms ease`,
                'box-sizing:border-box'
            ].join(';');

            cursor.style.cssText = [
                'position:absolute',
                'left:0',
                'top:0',
                'width:24px',
                'height:24px',
                'border:3px solid #fff',
                'border-radius:50%',
                'background:#ff2d55',
                'box-shadow:0 2px 8px rgba(0,0,0,.45)',
                'z-index:20',
                'transform-origin:center',
                `transform:translate(${previous.x}px, ${previous.y}px) translate(-50%, -50%)`,
                'transition:none',
                'box-sizing:border-box'
            ].join(';');

            const labelX = Math.min(Math.max(centerX + 18, 12), window.innerWidth - 132);
            const labelY = Math.min(Math.max(rectangle.top - 42, 12), window.innerHeight - 42);
            label.textContent = action;
            label.style.cssText = [
                'position:absolute',
                `left:${labelX}px`,
                `top:${labelY}px`,
                'padding:6px 10px',
                'border-radius:999px',
                'background:rgba(17,24,39,.95)',
                'color:#fff',
                'z-index:25',
                'font-size:13px',
                'font-weight:700',
                'line-height:1',
                'white-space:nowrap',
                'box-shadow:0 2px 8px rgba(0,0,0,.25)'
            ].join(';');

            root.style.display = 'block';
            root.style.opacity = '1';
            window.requestAnimationFrame(() => {
                focus.style.transform = 'scale(1)';
                cursor.style.transition = `transform ${Math.min(duration, 700)}ms cubic-bezier(.2,.75,.3,1)`;
                cursor.style.transform = `translate(${centerX}px, ${centerY}px) translate(-50%, -50%)`;
            });

            root.dataset.cursorX = String(centerX);
            root.dataset.cursorY = String(centerY);
            window.__pomDemoCursorPosition = {x: centerX, y: centerY};
            """;

    private static final String CLICK_FEEDBACK_SCRIPT = """
            const root = document.getElementById('pom-demo-overlay');
            if (!root) {
                return;
            }

            const x = Number(root.dataset.cursorX || 36);
            const y = Number(root.dataset.cursorY || 36);
            const ripple = document.createElement('div');
            ripple.style.cssText = [
                'position:absolute',
                `left:${x}px`,
                `top:${y}px`,
                'width:30px',
                'height:30px',
                'border:4px solid #ff2d55',
                'border-radius:50%',
                'transform:translate(-50%, -50%) scale(.25)',
                'opacity:.95',
                'box-sizing:border-box'
            ].join(';');
            root.appendChild(ripple);
            ripple.animate(
                [
                    {transform:'translate(-50%, -50%) scale(.25)', opacity:.95},
                    {transform:'translate(-50%, -50%) scale(1.9)', opacity:0}
                ],
                {duration:420, easing:'ease-out', fill:'forwards'}
            );
            setTimeout(() => ripple.remove(), 500);
            """;

    private final WebDriver driver;

    public DemoVisualizer(WebDriver driver) {
        this.driver = Objects.requireNonNull(driver, "driver must not be null");
    }

    public void showAction(WebElement element, String action) {
        if (!TestConfig.demo()) {
            return;
        }

        ((JavascriptExecutor) driver).executeScript(
                SHOW_ACTION_SCRIPT,
                element,
                action,
                TestConfig.demoCursorDurationMs(),
                browserTitle(),
                currentTestName());
        waitDuringDemo(TestConfig.demoCursorDurationMs());
    }

    public void showTestTitle() {
        if (TestConfig.demo()) {
            ((JavascriptExecutor) driver).executeScript(
                    SHOW_TITLE_SCRIPT,
                    browserTitle(),
                    currentTestName());
        }
    }

    public void clickFeedback() {
        if (TestConfig.demo()) {
            ((JavascriptExecutor) driver).executeScript(CLICK_FEEDBACK_SCRIPT);
        }
    }

    public void afterAction() {
        if (TestConfig.demo()) {
            waitDuringDemo(TestConfig.demoStepDelayMs());
        }
    }

    /**
     * Keeps the demo timing while allowing the viewport recorder to capture
     * frames from the same thread that owns the WebDriver session.
     */
    public void waitDuringDemo(long milliseconds) {
        if (milliseconds <= 0) {
            return;
        }

        DemoVideoRecorder recorder = DemoVideoRecorder.active();
        if (recorder != null) {
            recorder.captureFor(milliseconds);
            return;
        }

        pause(milliseconds);
    }

    public static void pause(long milliseconds) {
        if (milliseconds <= 0) {
            return;
        }

        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("The demo pause was interrupted.", exception);
        }
    }

    public static void setTestName(String testName) {
        TEST_NAME.set(Objects.requireNonNullElse(testName, "Demo"));
    }

    public static void clearTestName() {
        TEST_NAME.remove();
    }

    private static String currentTestName() {
        return TEST_NAME.get() == null ? "Demo" : TEST_NAME.get();
    }

    private static String browserTitle() {
        String runId = TestConfig.demoRunId();
        if (runId.isBlank()) {
            return "POM Demo - " + currentTestName();
        }
        return "POM Demo - " + runId + " - " + currentTestName();
    }
}
