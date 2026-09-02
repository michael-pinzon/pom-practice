package com.globant.pompractice.pages;

import java.time.Duration;
import java.util.Objects;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.globant.pompractice.config.DemoVisualizer;
import com.globant.pompractice.config.TestConfig;

public abstract class BasePage {

    protected static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(15);

    protected final WebDriver driver;
    protected final WebDriverWait wait;
    protected final DemoVisualizer demoVisualizer;

    protected BasePage(WebDriver driver) {
        this.driver = Objects.requireNonNull(driver, "driver must not be null");
        this.wait = new WebDriverWait(driver, DEFAULT_TIMEOUT);
        this.demoVisualizer = new DemoVisualizer(driver);
        PageFactory.initElements(driver, this);
    }

    protected void click(WebElement element) {
        WebElement clickableElement = wait.until(ExpectedConditions.elementToBeClickable(element));
        demoVisualizer.showAction(clickableElement, "Clic");
        demoVisualizer.clickFeedback();
        clickableElement.click();
        demoVisualizer.afterAction();
    }

    protected void clickWithJavaScript(WebElement element) {
        WebElement clickableElement = wait.until(ExpectedConditions.elementToBeClickable(element));
        demoVisualizer.showAction(clickableElement, "Clic");
        demoVisualizer.clickFeedback();
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center'}); arguments[0].click();",
                clickableElement);
        demoVisualizer.afterAction();
    }

    protected void type(WebElement element, String value) {
        WebElement visibleElement = wait.until(ExpectedConditions.visibilityOf(element));
        Objects.requireNonNull(value, "value must not be null");

        if (TestConfig.demo()) {
            demoVisualizer.showAction(visibleElement, "Escribir");
            demoVisualizer.clickFeedback();
            visibleElement.click();
            visibleElement.clear();
            for (int index = 0; index < value.length(); index++) {
                visibleElement.sendKeys(String.valueOf(value.charAt(index)));
                demoVisualizer.waitDuringDemo(TestConfig.demoTypingDelayMs());
            }
            demoVisualizer.afterAction();
            return;
        }

        ((JavascriptExecutor) driver).executeScript(
                "const element = arguments[0];"
                        + "const valueSetter = Object.getOwnPropertyDescriptor("
                        + "HTMLInputElement.prototype, 'value').set;"
                        + "valueSetter.call(element, arguments[1]);"
                        + "element.dispatchEvent(new Event('input', {bubbles: true}));"
                        + "element.dispatchEvent(new Event('change', {bubbles: true}));",
                visibleElement,
                value);
    }

    protected String readText(WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element)).getText();
    }

    protected boolean isVisible(WebElement element) {
        try {
            return wait.until(ExpectedConditions.visibilityOf(element)).isDisplayed();
        } catch (TimeoutException exception) {
            return false;
        }
    }

    protected boolean isUrl(String expectedUrl) {
        try {
            return wait.until(ExpectedConditions.urlToBe(expectedUrl));
        } catch (TimeoutException exception) {
            return false;
        }
    }

    protected boolean urlContains(String fragment) {
        try {
            return wait.until(ExpectedConditions.urlContains(fragment));
        } catch (TimeoutException exception) {
            return false;
        }
    }
}
