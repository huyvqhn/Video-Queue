package com.videoqueue.videoqueueprocessing.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@RequestMapping("videos")
public class VideoController {

    @Value("${upload.directory}")
    private String uploadDirectory;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private Queue video480pQueue;

    @Autowired
    private Queue video720pQueue;

    private static final Logger logger = LoggerFactory.getLogger(VideoController.class);

    @GetMapping("/hello")
    public String sayHello(@RequestParam String filePath) {
        filePath = "D:\\017-LAP-TRINH\\06-MikaylVideoProcess\\dummy-video.mp4";
        String resolution = "480";
//        processVideoWithResolution("uploads", filePath, resolution);

        return "Hi";
    }

    @PostMapping("/upload")
    public String uploadVideo(@RequestParam("file") MultipartFile file)  {

        String filePath = uploadDirectory + "/" + file.getOriginalFilename();
        try {
            // Save the uploaded file to the specified directory
            File dest = new File(filePath);

            dest.getParentFile().mkdirs(); // Create directories if they don't exist
            file.transferTo(dest);

            // Send tasks to RabbitMQ queues
            rabbitTemplate.convertAndSend(video480pQueue.getName(), filePath);
            rabbitTemplate.convertAndSend(video720pQueue.getName(), filePath);
        } catch (Exception e) {
            System.out.printf("e");
        }

        return "Video uploaded successfully";
    }
}
