package com.videoqueue.videoqueueprocessing.controller;

import com.videoqueue.videoqueueprocessing.model.Resolution;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@RequestMapping("videos")
public class VideoController {

    @Value("${processed.directory}")
    private String processedDirectory;

    @Value("${receive.directory}")
    private String receiveDirectory;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private Queue video480pQueue;

    @Autowired
    private Queue video720pQueue;

    private static final Logger logger = LoggerFactory.getLogger(VideoController.class);

    @PostMapping("/upload")
    public ResponseEntity<String> uploadVideo(@RequestParam("file") MultipartFile file,
                                              @RequestParam("resolution") String resolution)
        throws Exception {

        String filePath = receiveDirectory + file.getOriginalFilename();
        if (StringUtils.isEmpty(resolution)) {
            throw new Exception("The input resolution is empty");
        }
        if (!resolution.equals(Resolution.R480.name()) && !resolution.equals(Resolution.R720.name())) {
            throw new IllegalArgumentException("The input resolution is not supported");
        }

        try {
            new File(processedDirectory).mkdirs();

            // Save the uploaded file to the specified directory
            File dest = new File(filePath);
            dest.getParentFile().mkdirs(); // Create directories if they don't exist
            file.transferTo(dest);

            // Send tasks to RabbitMQ queues based on resolution
            if (Resolution.R480.name().equals(resolution)) {
                rabbitTemplate.convertAndSend(video480pQueue.getName(), filePath);
            } else if (Resolution.R720.name().equals(resolution)) {
                rabbitTemplate.convertAndSend(video720pQueue.getName(), filePath);
            }
        } catch (Exception e) {
            logger.error("Error uploading video: ", e);
            ResponseEntity.ok("Error uploading video");
        }

        return ResponseEntity.ok("Video uploaded successfully");
    }
}
