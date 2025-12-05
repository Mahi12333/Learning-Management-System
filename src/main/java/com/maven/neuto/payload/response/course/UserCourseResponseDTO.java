package com.maven.neuto.payload.response.course;


import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCourseResponseDTO {
    private Long id;
    private String name;
    private String description;
    private String about;
    private String type;
    private String imagesPath;
    private boolean archive;
    private int totalModules;
    private Long totalVideoDuration;
    private int totalLessons;
    private int completedLessons;
    private double progressPercent;
    private Long size;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
