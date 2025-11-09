package com.maven.neuto.config;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


@Configuration
public class CloudinaryConfiguration {
    @Value("${cloudinary.cloud-name}")
    private String cloudApiName;

    @Value("${cloudinary.api-key}")
    private String cloudApiKey;

    @Value("${cloudinary.api-secret}")
    private String cloudApiSecret;

    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", cloudApiName);
        config.put("api_key", cloudApiKey);
        config.put("api_secret", cloudApiSecret);

        return new Cloudinary(config);
    }
}
