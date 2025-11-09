package com.maven.neuto.payload.response.group;


import lombok.*;


@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GroupCommentResponseDTO {
      private Long id;
      private String content;
      private Long groupId;
      private Long parentId;
      private Long replyToComment;
      private Long replyToUser;
      private Long user;

}
