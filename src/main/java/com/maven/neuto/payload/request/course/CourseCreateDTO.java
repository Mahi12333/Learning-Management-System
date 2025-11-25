package com.maven.neuto.payload.request.course;

import com.maven.neuto.annotation.CustomeValidetion;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@CustomeValidetion
public class CourseCreateDTO {
    private String name;
    private String description;
    private String about;
    private List<String> tags;
    private Boolean archive;
    private String path;
    private Integer size;
}
