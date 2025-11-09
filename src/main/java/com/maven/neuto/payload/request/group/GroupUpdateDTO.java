package com.maven.neuto.payload.request.group;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Builder
public class GroupUpdateDTO {
    private String name;
    private String description;
    private String rule;
    private String about;
    private List<String> tags;
    private Long id;
    private Boolean onlyFetch;
    private String imagesPath;
    private Integer size;
}

