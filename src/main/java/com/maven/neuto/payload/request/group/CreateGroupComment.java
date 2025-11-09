package com.maven.neuto.payload.request.group;

import com.maven.neuto.annotation.CustomeValidetion;
import com.maven.neuto.annotation.OptionalField;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@CustomeValidetion
public class CreateGroupComment {
    private Long groupId;
    private String content;
    @OptionalField
    private Long parentId;
    @OptionalField
    private Long replyToUserId;
    @OptionalField
    private Long replyToCommentId;
}
