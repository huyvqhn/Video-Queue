package com.videoqueue.videoqueueprocessing.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class QueueLockService {

    private final ReentrantLock lock = new ReentrantLock();

    public void lock() {
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }

    public boolean tryLock() {
        return lock.tryLock();
    }
}
