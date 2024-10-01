package com.videoqueue.videoqueueprocessing.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Queue;

@Configuration
public class RabbitMQConfig {

    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange("video_queue_exchange");
    }

    @Bean
    public Queue video480pQueue() {
        return new Queue("video_480p", true);
    }

    @Bean
    public Queue video720pQueue() {
        return new Queue("video_720p", true);
    }

    @Bean
    public Binding binding480p(FanoutExchange fanoutExchange, Queue video480pQueue) {
        return BindingBuilder.bind(video480pQueue).to(fanoutExchange);
    }

    @Bean
    public Binding binding720p(FanoutExchange fanoutExchange, Queue video720pQueue) {
        return BindingBuilder.bind(video720pQueue).to(fanoutExchange);
    }
}
