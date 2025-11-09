package com.maven.neuto.payload.request.course;


import com.maven.neuto.annotation.CustomeValidetion;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@CustomeValidetion
public class ModuleCreateDTO {
    private String name;
    private Boolean archive;
}
