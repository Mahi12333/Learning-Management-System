package com.maven.neuto.payload.request.course;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CommentDTO {
    private Long courseId;
    private Long parentId = null;
    private Long replyToCommentId = null;
    private String type;
}
