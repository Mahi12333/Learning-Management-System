package com.maven.neuto.payload.request.course;

import com.maven.neuto.annotation.CustomeValidetion;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;


@Setter
@Getter
@CustomeValidetion
public class LessonCreateDTO {
    private String description;
    private String path;
    private Integer size;
    private Long moduleId;
    private List<String> tags;
    private Boolean archive;
    private List<QuizQuestion> quize;

}
