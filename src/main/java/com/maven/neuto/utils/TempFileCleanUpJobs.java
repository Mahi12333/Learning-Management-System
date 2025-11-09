package com.maven.neuto.utils;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class TempFileCleanUpJobs {
    private final Cloudinary cloudinary;

    // Run every night at 2 AM
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupTempFolder() throws Exception {
        Map result = cloudinary.api().deleteResourcesByPrefix("temp/", ObjectUtils.emptyMap());
        log.info("Temp folder cleanup result: {}", result);
    }
}
