package com.maven.neuto.config;

import org.slf4j.MDC;
import org.slf4j.spi.MDCAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggingConfig {
    // Can configure Logback appenders, MDC, async logging ---
    // I am use logback-spring.xml for logback configuration
//    @Bean
//    public MDCAdapter mdcAdapter() {
//        return MDC.getMDCAdapter();
//    }
}
