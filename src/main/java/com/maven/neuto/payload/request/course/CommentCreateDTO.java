package com.maven.neuto.payload.request.course;


import com.maven.neuto.annotation.CustomeValidetion;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@CustomeValidetion
public class CommentCreateDTO {
    private Long courseId;
    private String Content;
    private Long parentId = null;
    private Long replyToUserId = null;
    private Long replyToCommentId = null;
}
