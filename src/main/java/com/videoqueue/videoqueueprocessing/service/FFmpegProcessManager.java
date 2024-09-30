package com.videoqueue.videoqueueprocessing.service;

import java.io.IOException;

/**
 * @author Huy.VoQuang
 */
public class FFmpegProcessManager {
    private Process ffmpegProcess;

    public void startFFmpeg(String[] command) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        ffmpegProcess = processBuilder.start();
    }

    public void pauseFFmpeg() throws IOException {
        if (ffmpegProcess != null) {
            String pid = Long.toString(ffmpegProcess.pid());
            new ProcessBuilder("cmd", "/c", "pause", pid).start();
        }
    }

    public void resumeFFmpeg() throws IOException {
        if (ffmpegProcess != null) {
            String pid = Long.toString(ffmpegProcess.pid());
            new ProcessBuilder("cmd", "/c", "resume", pid).start();
        }
    }
}
