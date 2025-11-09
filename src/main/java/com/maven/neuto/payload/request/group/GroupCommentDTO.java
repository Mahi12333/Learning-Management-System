package com.maven.neuto.payload.request.group;


import com.maven.neuto.annotation.CustomeValidetion;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@CustomeValidetion
public class GroupCommentDTO {
    private Long groupId;
    private Long parentId = null;
    private Long replyToCommentId = null;
    private String type;
}
