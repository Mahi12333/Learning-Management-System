package com.maven.neuto.payload.response.group;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentSimpleDTO {
    private Long id;
    private String content;
}
