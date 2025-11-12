package com.maven.neuto.payload.response.group;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CommentResponseDTO {
    private Long id;
    private String content;
    private String timeAgo;
    private Integer likeCount;
    private Boolean isLikedByMe;
    private Boolean editAccess;
    private Boolean deleteAccess;
    private UserDTO user;
    private UserDTO replyToUser;
    private CommentSimpleDTO replyToComment;
    private List<CommentResponseDTO> replies;
    private Integer replyCount;
    private Boolean hasMoreReplies;
}
