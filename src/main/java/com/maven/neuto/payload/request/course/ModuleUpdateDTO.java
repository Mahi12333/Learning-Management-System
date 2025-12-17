package com.maven.neuto.payload.request.course;

import com.maven.neuto.annotation.CustomeValidetion;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ModuleUpdateDTO {
    private String name;
    private Long moduleId;
    private Boolean archive;
    private Boolean onlyFetch;
    private Long courseId;
}
