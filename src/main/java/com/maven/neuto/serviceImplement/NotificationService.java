package com.maven.neuto.serviceImplement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executor;

@Service
public class NotificationService {
    @Autowired
    private Executor cachedThreadPool;

    public void sendRealTimeNotification(String message) {
        cachedThreadPool.execute(() -> {
            //TODO
            // push WebSocket message
        });
    }
}
