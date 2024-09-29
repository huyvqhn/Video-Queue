package com.videoqueue.videoqueueprocessing.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VideoController {

    @GetMapping({"/hello"})
    public String sayHello() {
        return "Hi";
    }
}
