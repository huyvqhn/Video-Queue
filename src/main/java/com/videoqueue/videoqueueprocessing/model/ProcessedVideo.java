package com.videoqueue.videoqueueprocessing.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "processed_videos")
@Setter @Getter
public class ProcessedVideo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String filename;
    private String resolution;
}
