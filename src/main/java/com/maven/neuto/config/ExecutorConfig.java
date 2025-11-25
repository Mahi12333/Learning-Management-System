package com.maven.neuto.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Slf4j
@Configuration
public class ExecutorConfig {
    /*
    You can use EITHER
    Executors.newVirtualThreadPerTaskExecutor()
    OR
    Thread.ofVirtual().factory()
    Both create virtual threads.
     */

    @Bean
    public Executor virtualExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    @Bean
    public Executor cpuExecutor() {
        log.info("Initializing Fixed Thread Pool Executor with 8 threads for CPU-bound tasks");
        return Executors.newFixedThreadPool(8);  // CPU tasks
    }

    @Bean
    public ScheduledExecutorService scheduler() {
        log.info("Initializing Scheduled Executor Service with 3 threads for cron tasks");
        return Executors.newScheduledThreadPool(3); // Cron tasks
    }
}
