package com.maven.neuto.payload.request.course;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class LessonSaveProgress {
    private String lessonSlug;
    private Float progress;
    private String status;
}
