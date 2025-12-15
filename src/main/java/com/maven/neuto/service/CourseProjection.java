package com.maven.neuto.service;

import java.time.LocalDateTime;

public interface CourseProjection {
    Long getId();
    String getName();
    String getDescription();
    String getAbout();
    String getTags();
    String getImagesPath();
    Long getSize();
    Boolean getArchive();
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();
    Long getTotalModules();
    Long getTotalLessons();
    Long getTotalDuration();
}
