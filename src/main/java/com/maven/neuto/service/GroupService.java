package com.maven.neuto.service;

import com.maven.neuto.payload.request.group.CreateGroupComment;
import com.maven.neuto.payload.request.group.GroupCreateDTO;
import com.maven.neuto.payload.request.group.GroupUpdateDTO;
import com.maven.neuto.payload.response.PaginatedResponse;
import com.maven.neuto.payload.response.group.CommentResponseDTO;
import com.maven.neuto.payload.response.group.GroupCommentResponseDTO;
import com.maven.neuto.payload.response.group.GroupResponseDTO;


public interface GroupService {
    GroupResponseDTO createGroup(GroupCreateDTO request);
    GroupResponseDTO updatedGroup(GroupUpdateDTO request);
    void deleteGroup(Long groupId);
    String joinLeaveGroup(Long groupId);
    GroupCommentResponseDTO createGroupComment(CreateGroupComment request);

    PaginatedResponse<CommentResponseDTO> fetchGroupComment(Long groupId, Long parentId, Long replyToCommentId, String type, Integer pageNumber, Integer pageSize, String sortOrder);
}
