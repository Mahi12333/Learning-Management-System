package com.maven.neuto.payload.request.course;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class LessonUpdateDTO {
    private String lessonSlug;
    private String description;
    private String path;
    private Integer size;
    private String moduleSlug;
    private List<String> tags;
    private Boolean archive;
    private Boolean onlyFetch;
}
