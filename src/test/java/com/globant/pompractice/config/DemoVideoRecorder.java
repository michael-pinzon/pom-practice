package com.globant.pompractice.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

/**
 * Records the browser viewport directly through WebDriver screenshots. Frames
 * are collected synchronously by the test thread and encoded after the test
 * from a numbered PNG sequence. This keeps Selenium thread-safe and avoids a
 * partially-written or empty MP4 when a pipe closes unexpectedly on Windows.
 */
public final class DemoVideoRecorder implements AutoCloseable {

    private static final long ENCODER_TIMEOUT_SECONDS = 60;
    // The launcher runs one test per Maven process, so one shared recorder is
    // safer than a ThreadLocal: TestNG may use a different thread for a
    // configuration method and the test method.
    private static volatile DemoVideoRecorder activeRecorder;

    private final WebDriver driver;
    private final Path videoPath;
    private final Path frameDirectory;
    private final int frameRate;
    private final long frameIntervalNanos;
    private final AtomicBoolean recording = new AtomicBoolean(true);
    private final AtomicBoolean closed = new AtomicBoolean(false);
    private volatile Exception captureFailure;
    private int frameCount;

    private DemoVideoRecorder(WebDriver driver, Path videoPath, int frameRate) throws IOException {
        this.driver = driver;
        this.videoPath = videoPath;
        this.frameRate = frameRate;
        this.frameIntervalNanos = Math.max(1L, TimeUnit.SECONDS.toNanos(1) / frameRate);

        if (videoPath.getParent() != null) {
            Files.createDirectories(videoPath.getParent());
        }
        Files.deleteIfExists(videoPath);
        frameDirectory = Files.createTempDirectory("pom-demo-frames-");
    }

    public static DemoVideoRecorder startIfConfigured(WebDriver driver) {
        if (!TestConfig.demo() || TestConfig.demoVideoPath().isBlank()) {
            return null;
        }

        try {
            DemoVideoRecorder recorder = new DemoVideoRecorder(
                    driver,
                    Path.of(TestConfig.demoVideoPath()).toAbsolutePath().normalize(),
                    TestConfig.demoFrameRate());
            activeRecorder = recorder;
            return recorder;
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "No se pudo preparar la grabacion del viewport. Verifica permisos de escritura.",
                    exception);
        }
    }

    /** Returns the recorder attached to the current Maven test process, if any. */
    public static DemoVideoRecorder active() {
        return activeRecorder;
    }

    /**
     * Captures an exact number of frames for the requested demo pause. Using a
     * fixed frame count keeps the playback speed consistent even if one
     * screenshot takes longer on a particular page.
     */
    public void captureFor(long milliseconds) {
        if (milliseconds <= 0 || closed.get() || !recording.get()) {
            return;
        }

        int framesToCapture = Math.max(1,
                (int) Math.ceil(milliseconds * frameRate / 1000.0));
        long startedAt = System.nanoTime();
        long durationNanos = TimeUnit.MILLISECONDS.toNanos(milliseconds);

        for (int index = 0; index < framesToCapture && recording.get() && !closed.get(); index++) {
            byte[] screenshot = captureScreenshot();
            if (screenshot == null) {
                return;
            }
            if (!writeFrame(screenshot)) {
                return;
            }

            long targetTime = startedAt + ((index + 1L) * durationNanos / framesToCapture);
            sleepUntil(targetTime);
        }
    }

    private byte[] captureScreenshot() {
        try {
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            if (screenshot == null || screenshot.length == 0) {
                throw new IOException("Selenium devolvio un screenshot vacio.");
            }
            return screenshot;
        } catch (WebDriverException | IOException exception) {
            captureFailure = exception;
            recording.set(false);
            return null;
        }
    }

    private boolean writeFrame(byte[] screenshot) {
        Path framePath = frameDirectory.resolve(
                String.format(Locale.ROOT, "frame-%06d.png", frameCount));
        try {
            Files.write(framePath, screenshot);
            if (Files.size(framePath) == 0) {
                throw new IOException("El archivo PNG generado quedo vacio.");
            }
            frameCount++;
            return true;
        } catch (IOException exception) {
            captureFailure = exception;
            recording.set(false);
            return false;
        }
    }

    private void sleepUntil(long targetTime) {
        long remainingNanos = targetTime - System.nanoTime();
        if (remainingNanos <= 0) {
            return;
        }

        try {
            TimeUnit.NANOSECONDS.sleep(Math.min(remainingNanos, frameIntervalNanos));
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            recording.set(false);
        }
    }

    @Override
    public void close() {
        if (!closed.compareAndSet(false, true)) {
            return;
        }
        recording.set(false);

        try {
            if (frameCount > 0) {
                encodeVideo();
            } else {
                System.err.println("Aviso: no se escribieron frames en la grabacion del viewport.");
            }
        } finally {
            cleanupFrames();
            if (activeRecorder == this) {
                activeRecorder = null;
            }
        }

        if (captureFailure != null) {
            System.err.println("Aviso: la captura del viewport se detuvo: " + captureFailure.getMessage());
        }
    }

    private void encodeVideo() {
        Path temporaryVideoPath = videoPath.resolveSibling(
                videoPath.getFileName().toString() + ".part.mp4");
        try {
            Files.deleteIfExists(temporaryVideoPath);
            Path inputPattern = frameDirectory.resolve("frame-%06d.png");
            List<String> ffmpegCommand = List.of(
                    "ffmpeg",
                    "-hide_banner",
                    "-loglevel", "error",
                    "-y",
                    "-framerate", String.valueOf(frameRate),
                    "-start_number", "0",
                    "-i", inputPattern.toString(),
                    "-vf", "pad=ceil(iw/2)*2:ceil(ih/2)*2",
                    "-c:v", "libx264",
                    "-preset", "veryfast",
                    "-crf", "18",
                    "-pix_fmt", "yuv420p",
                    "-movflags", "+faststart",
                    temporaryVideoPath.toAbsolutePath().normalize().toString());

            Process encoder = new ProcessBuilder(ffmpegCommand)
                    .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                    .redirectError(ProcessBuilder.Redirect.INHERIT)
                    .start();
            boolean finished = encoder.waitFor(ENCODER_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (!finished) {
                encoder.destroy();
                if (!encoder.waitFor(5, TimeUnit.SECONDS)) {
                    encoder.destroyForcibly();
                    encoder.waitFor();
                }
                System.err.println("Aviso: FFmpeg excedio el tiempo de cierre de la grabacion.");
                return;
            }
            if (encoder.exitValue() != 0) {
                System.err.println("Aviso: FFmpeg termino con codigo " + encoder.exitValue() + ".");
                return;
            }
            if (!Files.isRegularFile(temporaryVideoPath) || Files.size(temporaryVideoPath) == 0) {
                System.err.println("Aviso: FFmpeg no genero un MP4 valido.");
                return;
            }

            Files.move(temporaryVideoPath, videoPath,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            System.err.println("Aviso: no se pudo codificar la grabacion: " + exception.getMessage());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            System.err.println("Aviso: la codificacion de la grabacion fue interrumpida.");
        } finally {
            try {
                Files.deleteIfExists(temporaryVideoPath);
            } catch (IOException ignored) {
                // A later run can safely replace this temporary file.
            }
        }
    }

    private void cleanupFrames() {
        try (Stream<Path> paths = Files.walk(frameDirectory)) {
            paths.sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException ignored) {
                    // Temporary files are harmless if Windows still has one open.
                }
            });
        } catch (IOException ignored) {
            // The video is already finalized; cleanup is best effort.
        }
    }
}
