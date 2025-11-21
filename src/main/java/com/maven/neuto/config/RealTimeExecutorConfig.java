package com.maven.neuto.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
public class RealTimeExecutorConfig {
    /*
    Used for:
    WebSocket updates
    Student live status
    Real-time class notifications
     */
    @Bean
    public Executor cachedThreadPool() {
        return Executors.newCachedThreadPool();
    }
}
