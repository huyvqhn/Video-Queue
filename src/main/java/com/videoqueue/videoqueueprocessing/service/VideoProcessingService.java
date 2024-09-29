package com.videoqueue.videoqueueprocessing.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Service
public class VideoProcessingService {

    private static final Logger logger = LoggerFactory.getLogger(VideoProcessingService.class);
    public static final String UPLOADS = "uploads";

    @Autowired
    private QueueLockService queueLockService;

    @RabbitListener(queues = "video_480p")
    public void process480pVideo(String filePath) {
        if (queueLockService.tryLock()) {
            try {
                processVideoWithResolution(UPLOADS, filePath, "480p");
            } finally {
                logger.info("upload 480p file success");
                queueLockService.unlock();
            }
        }

    }

    @RabbitListener(queues = "video_720p")
    public void process720pVideo(String filePath) {
        if (queueLockService.tryLock()) {
            try {
                processVideoWithResolution(UPLOADS, filePath, "720p");
            } finally {
                logger.info("upload 720p file success");
                queueLockService.unlock();
            }
        }
    }

    private void processVideoWithResolution(String action, String filePath, String resolution) {

        String outputFilePath = filePath.replace(action, "processed").replace(".mp4", "_" + resolution + ".mp4");
        String[] command = {
                "ffmpeg",
                "-i",
                filePath,
                "-vf",
                "scale=-1:480",
                outputFilePath
        };
        try {
            // Create a ProcessBuilder instance
            ProcessBuilder processBuilder = new ProcessBuilder(command);

            // Redirect error stream to the standard output stream
            processBuilder.redirectErrorStream(true);

            // Start the process
            Process process = processBuilder.start();

            // Read the output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));


            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // Wait for the process to complete
            int exitCode = process.waitFor();
            reader.close();

            System.out.println("Exited with code: " + exitCode);
        } catch (Exception e) {
            logger.error("Error while processing video:  " + e.getCause().toString());
        }
    }
}
