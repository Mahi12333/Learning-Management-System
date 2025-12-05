package com.maven.neuto.payload.response.course;

import java.time.LocalDateTime;


public record OngoingCourseResponseDTO (
    Long id,
    String name,
    String description,
    String about,
    String type,
    String imagesPath,
    boolean archive,
    String slug,
    int totalModules,
    Long totalVideoDuration,
    int totalLessons,
    int completedLessons,
    double progressPercentage,
    Long size,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
)
{}