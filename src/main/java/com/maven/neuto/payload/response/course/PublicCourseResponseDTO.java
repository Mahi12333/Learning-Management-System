package com.maven.neuto.payload.response.course;


import java.time.LocalDateTime;
import java.util.List;

public record PublicCourseResponseDTO (
        Long id,
        String name,
        String description,
        String about,
        List<String> tags,
        String imagesPath,
        boolean archive,
        String slug,
        Long totalModules,
        Long totalVideoDuration,
        Long totalLessons,
        Long size,
        int completedLessons,
        double progressPercentage,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
)
{}
