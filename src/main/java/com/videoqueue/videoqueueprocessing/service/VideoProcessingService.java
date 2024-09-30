package com.videoqueue.videoqueueprocessing.service;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Service
public class VideoProcessingService {

    private static final Logger logger = LoggerFactory.getLogger(VideoProcessingService.class);
    public static final String UPLOADS = "uploads";
    public static final String FFMPEG_EXE = "D:\\ffmpeg\\bin\\ffmpeg.exe";
    public static final String SCALE_480 = "scale=-640:480";
    public static final String SCALE_720 = "scale=-1280:720";

    @Autowired
    private QueueLockService queueLockService;

    @Value("${processed.directory}")
    private String processedDirectory;

    @RabbitListener(queues = "video_480p")
    public void process480pVideo(String filePath) {
        if (queueLockService.tryLock()) {
            try {
                processVideoWithResolution(UPLOADS, filePath, this.fileNameFromPath(filePath), "480p");
            } finally {
                logger.info("upload 480p file success");
                queueLockService.unlock();
            }
        }
    }

    private String fileNameFromPath(String filePath) {
        return filePath.substring(filePath.lastIndexOf("\\") + 1);
    }

    @RabbitListener(queues = "video_720p")
    public void process720pVideo(String filePath) {
        if (queueLockService.tryLock()) {
            try {
                processVideoWithResolution(UPLOADS, filePath, this.fileNameFromPath(filePath), "720p");
            } finally {
                logger.info("upload 720p file success");
                queueLockService.unlock();
            }
        }
    }

    private void processVideoWithResolution(String action, String filePath, String fileName, String resolution) {

        String outputFilePath = processedDirectory + fileName.replace(action, "processed").replace(".mp4", "_" + resolution + ".mp4");
        String[] command = {
                FFMPEG_EXE,
                "-i",
                filePath,
                "-vf",
            Objects.equals(resolution, "480p") ? SCALE_480 : SCALE_720,
                outputFilePath
        };
        try {
            // Create a ProcessBuilder instance
            ProcessBuilder processBuilder = new ProcessBuilder(command);

            // Redirect error stream to the standard output stream
            processBuilder.redirectErrorStream(true);
            logger.info("Command call ffmpeg: " + processBuilder.command().toString());

            // Start the process
            Process process = processBuilder.start();

            // Read the output
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

            // Wait for the process to complete
//            int exitCode = process.waitFor();

        } catch (Exception e) {
            logger.error("Error while processing video:  " + e.getCause().toString());
            System.out.println("Error while processing video:  " + e.getCause().toString());
        }
    }
}
