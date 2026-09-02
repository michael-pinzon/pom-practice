package com.globant.pompractice.listeners;

import java.util.concurrent.atomic.AtomicInteger;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public final class TestProgressListener implements ITestListener {

    private static final int PROGRESS_BAR_WIDTH = 20;

    private final AtomicInteger completedTests = new AtomicInteger();
    private volatile int totalTests;

    @Override
    public void onStart(ITestContext context) {
        totalTests = Math.max(context.getAllTestMethods().length, 1);
        printProgress("Starting TestNG suite (" + totalTests + " tests)");
    }

    @Override
    public void onTestStart(ITestResult result) {
        int currentTest = completedTests.get() + 1;
        printProgress(
                progressBar(completedTests.get(), totalTests)
                        + " " + currentTest + "/" + totalTests
                        + " START " + testName(result));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        reportCompleted(result, "PASS");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        reportCompleted(result, "FAIL");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        reportCompleted(result, "SKIP");
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        reportCompleted(result, "RETRY");
    }

    @Override
    public void onFinish(ITestContext context) {
        printProgress(
                progressBar(completedTests.get(), totalTests)
                        + " Suite complete: " + completedTests.get() + "/" + totalTests + " tests finished");
    }

    private void reportCompleted(ITestResult result, String status) {
        int completed = completedTests.incrementAndGet();
        printProgress(
                progressBar(completed, totalTests)
                        + " " + completed + "/" + totalTests
                        + " " + status + " " + testName(result));
    }

    private void printProgress(String message) {
        System.out.println("[PROGRESS] " + message);
        System.out.flush();
    }

    private String testName(ITestResult result) {
        return result.getTestClass().getRealClass().getSimpleName()
                + "." + result.getMethod().getMethodName();
    }

    private String progressBar(int completed, int total) {
        if (total <= 0) {
            return "[>" + " ".repeat(PROGRESS_BAR_WIDTH - 1) + "]";
        }

        int filled = Math.min(
                PROGRESS_BAR_WIDTH,
                (int) Math.round((double) completed / total * PROGRESS_BAR_WIDTH));
        String marker = filled < PROGRESS_BAR_WIDTH ? ">" : "";
        int spaces = PROGRESS_BAR_WIDTH - filled - marker.length();

        return "[" + "=".repeat(filled) + marker + " ".repeat(Math.max(spaces, 0)) + "]";
    }
}
