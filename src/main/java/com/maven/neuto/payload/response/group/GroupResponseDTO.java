package com.maven.neuto.payload.response.group;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GroupResponseDTO {
    private Long id;
    private String name;
    private String description;
    private String rule;
    private String about;
    private List<String> tags;
    private String imagesPath;
    private Integer size;
}
