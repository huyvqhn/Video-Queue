package com.videoqueue.videoqueueprocessing.service;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;

@Service
public class VideoProcessingService {

    private static final Logger logger = LoggerFactory.getLogger(VideoProcessingService.class);
    public static final String UPLOADS = "uploads";
    public static final String FFMPEG_EXE = "C:\\ffmpeg\\bin\\ffmpeg.exe";
    public static final String SCALE_480 = "scale=-640:480";
    public static final String SCALE_720 = "scale=-1280:720";

    @Autowired
    private QueueLockService queueLockService;

    @Value("${processed.directory}")
    private String processedDirectory;

    private final Object lock = new Object();
    private boolean isProcessing480p = false;

    @Autowired
    private FFmpegProcessManager ffmpegProcessManager720;
    @Autowired
    private FFmpegProcessManager ffmpegProcessManager480;

    @RabbitListener(queues = "video480p")
    public void process480pVideo(Message message, Channel channel) throws Exception {
        synchronized (lock) {
            isProcessing480p = true;
            if (ffmpegProcessManager720.isRunning()) {
                ffmpegProcessManager720.pauseFFmpeg();
            }
        }
        try {
            String filePath = new String(message.getBody());
            processVideoWithResolution(UPLOADS, filePath, this.fileNameFromPath(filePath), "480p");
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
        } finally {
            synchronized (lock) {
                isProcessing480p = false;
                lock.notifyAll();
                if (ffmpegProcessManager720.isPaused()) {
                    ffmpegProcessManager720.resumeFFmpeg();
                }
            }
        }
    }

    @RabbitListener(queues = "video720p")
    public void process720pVideo(Message message, Channel channel) throws Exception {
        synchronized (lock) {
            while (isProcessing480p) {
                lock.wait();
            }
        }
        try {
            String filePath = new String(message.getBody());
            processVideoWithResolution(UPLOADS, filePath, this.fileNameFromPath(filePath), "720p");
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
        }
    }

    private String fileNameFromPath(String filePath) {
        return filePath.substring(filePath.lastIndexOf("\\") + 1);
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
            if (Objects.equals(resolution, "720p")) {
                ffmpegProcessManager720.startFFmpeg(command);
            } else if (resolution.equals("480p")) {
                ffmpegProcessManager480.startFFmpeg(command);
            }

        } catch (Exception e) {
            logger.error("Error while processing video:  " + e.getCause().toString());
            System.out.println("Error while processing video:  " + e.getCause().toString());
        }
    }
}
