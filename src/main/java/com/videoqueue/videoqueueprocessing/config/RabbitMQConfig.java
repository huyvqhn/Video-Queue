package com.videoqueue.videoqueueprocessing.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Queue;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue video480pQueue() {
        return new Queue("video_480p", true);
    }

    @Bean
    public Queue video720pQueue() {
        return new Queue("video_720p", true);
    }
}
