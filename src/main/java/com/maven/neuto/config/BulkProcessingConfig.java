package com.maven.neuto.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
public class BulkProcessingConfig {
    /*
    Used for:
    Import 10,000 students CSV
    Re-evaluate all assignments
    Generate weekly reports
     */
    @Bean
    public Executor fixedThreadPool() {
        return Executors.newFixedThreadPool(8);
    }
}
