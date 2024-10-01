package com.videoqueue.videoqueueprocessing.service;

import java.io.*;

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
public class FFmpegProcessManager {

    private static final Logger logger = LoggerFactory.getLogger(VideoProcessingService.class);

    private Process ffmpegProcess;
    private String resolution;
    private String fileName;
    @Getter
    private boolean paused = false;

    public void startFFmpeg(String[] command) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        // Redirect error stream to the standard output stream
        processBuilder.redirectErrorStream(true);
        String commandResolution = command[4];
        String commandFileName = command[5];
        this.resolution = commandResolution.substring(commandResolution.indexOf(":") + 1);
        this.fileName = commandFileName.substring(commandFileName.lastIndexOf("\\") + 1);
        ffmpegProcess = processBuilder.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(this.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
    }

    public void pauseFFmpeg() throws IOException {
        if (ffmpegProcess != null) {
            String pid = Long.toString(ffmpegProcess.pid());
            logger.info(this.getFileName() + " processing is paused");
            new ProcessBuilder("cmd", "/c", "pause", pid).start();
            paused = true;
        }
    }

    public void resumeFFmpeg() throws IOException {
        if (ffmpegProcess != null) {
            String pid = Long.toString(ffmpegProcess.pid());
            logger.info(this.getFileName() + " processing is resumed");
            new ProcessBuilder("cmd", "/c", "resume", pid).start();
            this.paused = false;
        }
    }

    public boolean isRunning() {
        return ffmpegProcess != null && ffmpegProcess.isAlive();
    }

    public InputStream getInputStream() {
        return this.ffmpegProcess.getInputStream();
    }
}
