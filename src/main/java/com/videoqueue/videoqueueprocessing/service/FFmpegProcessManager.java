package com.videoqueue.videoqueueprocessing.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author Huy.VoQuang
 */
@Component
@Getter
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class FFmpegProcessManager extends Process {

    private static final Logger logger = LoggerFactory.getLogger(VideoProcessingService.class);

    private Process ffmpegProcess;

    private String resolution;

    public void startFFmpeg(String[] command) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        // Redirect error stream to the standard output stream
        processBuilder.redirectErrorStream(true);
        //        logger.info("Command call ffmpeg: " + processBuilder.command().toString());

        String resolution = command[4];
        this.resolution = resolution.substring(resolution.indexOf(":") + 1);
        ffmpegProcess = processBuilder.start();
    }

    public void pauseFFmpeg() throws IOException {
        if (ffmpegProcess != null) {
            String pid = Long.toString(ffmpegProcess.pid());
            logger.info(pid + " is paused");
            new ProcessBuilder("cmd", "/c", "pause", pid).start();
        }
    }

    public void resumeFFmpeg() throws IOException {
        if (ffmpegProcess != null) {
            String pid = Long.toString(ffmpegProcess.pid());
            logger.info(pid + " is resumed");
            new ProcessBuilder("cmd", "/c", "resume", pid).start();
        }
    }

    public boolean isRunning() {
        return ffmpegProcess != null && ffmpegProcess.isAlive();
    }

    public boolean isPaused() {
        // Implement logic to check if the process is paused
        return false;
    }

    @Override
    public OutputStream getOutputStream() {
        return null;
    }

    public InputStream getInputStream() {
        return this.ffmpegProcess.getInputStream();
    }

    @Override
    public InputStream getErrorStream() {
        return null;
    }

    @Override
    public int waitFor() throws InterruptedException {
        return 0;
    }

    @Override
    public int exitValue() {
        return 0;
    }

    @Override
    public void destroy() {

    }
}
