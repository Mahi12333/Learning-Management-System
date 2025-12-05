package com.maven.neuto.service;

import java.time.LocalDateTime;

public interface CourseProjection {
    Long getId();
    String getName();
    String getDescription();
    String getAbout();
    String getTags();
    String getImagesPath();
    LocalDateTime getCreatedAt();
    Integer getTotalModules();
    Integer getTotalLessons();
    Long getTotalDuration();
}
