package com.videoqueue.videoqueueprocessing.repository;

import com.videoqueue.videoqueueprocessing.model.ProcessedVideo;
import org.springframework.data.repository.CrudRepository;

public interface ProcessedVideoRepository extends CrudRepository<ProcessedVideo, Long> {
}
