package com.maven.neuto.serviceImplement;

import com.google.gson.Gson;
import com.maven.neuto.emun.GroupStatus;
import com.maven.neuto.exception.APIException;
import com.maven.neuto.exception.ResourceNotFoundException;
import com.maven.neuto.mapstruct.GroupMapper;
import com.maven.neuto.mapstruct.MapperContext;
import com.maven.neuto.model.*;
import com.maven.neuto.payload.request.group.CreateGroupComment;
import com.maven.neuto.payload.request.group.GroupCreateDTO;
import com.maven.neuto.payload.request.group.GroupUpdateDTO;
import com.maven.neuto.payload.response.PaginatedResponse;
import com.maven.neuto.payload.response.content.UserShortContentResponseDTO;
import com.maven.neuto.payload.response.group.CommentResponseDTO;
import com.maven.neuto.payload.response.group.GroupCommentResponseDTO;
import com.maven.neuto.payload.response.group.GroupResponseDTO;
import com.maven.neuto.payload.response.group.UserDTO;
import com.maven.neuto.repository.*;
import com.maven.neuto.service.GroupService;
import com.maven.neuto.utils.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupServiceImple implements GroupService {
    private final GroupRepository groupRepository;
    private final AuthUtil authUtil;
    private final CommunityRepository communityRepository;
    private final GroupMapper groupMapper;
    private final UserRepository userRepository;
    private final GroupUserRepository groupUserRepository;
    private final MapperContext mapperContext;
    private final GroupCommentRepository groupCommentRepository;

    @Override
    public GroupResponseDTO createGroup(GroupCreateDTO request) {
        Long currentUserId = authUtil.loggedInUserIdForTesting();
        Long communityId = authUtil.communityId();
        Community community = communityRepository.findById(communityId).orElseThrow(()-> new ResourceNotFoundException("Community not found"));
        if (groupRepository.existsByName(request.getName())) {
            throw new APIException("Group name already exists.", HttpStatus.BAD_REQUEST);
        }

        Group group = groupMapper.toEntity(request, communityId, currentUserId, mapperContext);
        Group saveGroup = groupRepository.save(group);

        GroupUser groupUser = new GroupUser();
        groupUser.setUserId(currentUserId);
        groupUser.setGroup(saveGroup.getId());
        groupUser.setStatus(GroupStatus.SUCCESS);
        groupUser.setJoinedAt(LocalDateTime.now());
        groupUserRepository.save(groupUser);
        return groupMapper.toResponseDto(saveGroup);
    }

    @Override
    public GroupResponseDTO updatedGroup(GroupUpdateDTO request) {
        Long groupId = request.getId();
        String newName = request.getName();
        if(groupId == null){
            throw new APIException("Group id is required", HttpStatus.BAD_REQUEST);
        }
        Group existingGroup = groupRepository.findById(groupId).orElseThrow(()-> new ResourceNotFoundException("Group not found"));
        if(Boolean.TRUE.equals(request.getOnlyFetch())){
            return groupMapper.toResponseDto(existingGroup);
        }

//        int updatedRows = groupRepository.updateGroup(request.getId(), newName);
//        if (updatedRows == 0) {
//            throw new APIException("Group update failed", HttpStatus.INTERNAL_SERVER_ERROR);
//        }

//        Group updatedGroup = groupRepository.findById(request.getId())
//                .orElseThrow(() -> new ResourceNotFoundException("Group not found after update"));


        groupMapper.updateGroupFromDto(request, existingGroup);
        if (request.getTags() != null) {
            existingGroup.setTags(request.getTags());
        }
        Group savedGroup = groupRepository.save(existingGroup);
        return groupMapper.toResponseDto(savedGroup);
    }

    @Override
    public void deleteGroup(Long groupId) {
        Group existingGroup = groupRepository.findById(groupId).orElseThrow(()-> new ResourceNotFoundException("group.not.found"));
        groupRepository.delete(existingGroup);
    }

    @Override
    public String joinLeaveGroup(Long groupId) {
        Group existingGroup = groupRepository.findById(groupId).orElseThrow(()-> new ResourceNotFoundException("Group not found"));
        Long currentUserId = authUtil.loggedInUserId();
        Optional<GroupUser> userExisting = groupUserRepository.findByUserIdAndGroupId(currentUserId, groupId);
        if(userExisting.isPresent()){
           groupUserRepository.delete(userExisting.get());
           return "You have successfully left from the group";
        }else {
            GroupUser newGroupUser = new GroupUser();
            newGroupUser.setGroup(groupId);
            newGroupUser.setUserId(currentUserId);
            newGroupUser.setStatus(GroupStatus.SUCCESS);
            return "You have successfully joined to the group";
        }
    }

    @Override
    public GroupCommentResponseDTO createGroupComment(CreateGroupComment request) {
        Long currentUser = authUtil.loggedInUserIdForTesting();
        var parentId = groupCommentRepository.findByParentId(request.getParentId()).orElseThrow(() -> new ResourceNotFoundException("group.parentId.not.found"));
        var replyToCommentId = groupCommentRepository.findById(request.getReplyToCommentId()).orElseThrow(() -> new ResourceNotFoundException("replyTo.commentId.not.found"));

        GroupComment saveComment = new GroupComment();
        saveComment.setGroup(groupRepository.getReferenceById(request.getGroupId()));
        saveComment.setReplyToComment(groupCommentRepository.getReferenceById(request.getReplyToCommentId()));
        saveComment.setCreatedAt(Instant.now());
        saveComment.setContent(request.getContent());
        saveComment.setReplyToUser(userRepository.getReferenceById(request.getReplyToUserId()));
        saveComment.setUpdatedAt(Instant.now());
        saveComment.setParent(groupCommentRepository.getReferenceById(request.getParentId()));
        saveComment.setUser(userRepository.getReferenceById(currentUser));

       GroupComment saved = groupCommentRepository.save(saveComment);

        return groupMapper.toGroupCommentResponseDTO(saved);
    }

    @Override
    public PaginatedResponse<CommentResponseDTO> fetchGroupComment(Long groupId, Long parentId, Long replyToCommentId, String type, Integer pageNumber, Integer pageSize, String sortOrder) {
        Long loggedInUserId = authUtil.loggedInUserId();
        User currentUser = userRepository.findById(loggedInUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));

        boolean isAdmin = currentUser.getRole().getId() == 2;

        if (type == null) {
            Sort.Direction direction = "DESC".equalsIgnoreCase(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(pageNumber != null ? pageNumber : 0,
                    pageSize != null ? pageSize : 10,
                    Sort.by(direction, "createdAt"));

            Page<GroupComment> commentsPage = groupCommentRepository
                    .findByGroup_IdAndParent_Id(groupId, parentId, pageable);

            List<CommentResponseDTO> commentDTOs = commentsPage.getContent().stream()
                    .map(comment -> mapToDTO(comment, currentUser, isAdmin))
                    .collect(Collectors.toList());

            return PaginatedResponse.<CommentResponseDTO>builder()
                    .content(commentDTOs)
                    .pageNumber(commentsPage.getNumber())
                    .pageSize(commentsPage.getSize())
                    .totalElements(commentsPage.getTotalElements())
                    .totalPages(commentsPage.getTotalPages())
                    .build();


        }

        // else if(type == 'notification' && replyToCommentId != null)
        else if ("notification".equals(type) && replyToCommentId != null) {
            GroupComment replyComment = groupCommentRepository.findById(replyToCommentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Reply comment not found"));

            Long parentIdOfReply = replyComment.getReplyToComment().getId();

            Sort.Direction direction = "DESC".equalsIgnoreCase(sortOrder)
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;

            Pageable pageable = PageRequest.of(
                    pageNumber != null && pageNumber > 0 ? pageNumber - 1 : 0,
                    pageSize != null && pageSize > 0 ? pageSize : 10,
                    Sort.by(direction, "createdAt")
            );

            Page<GroupComment> repliesPage = groupCommentRepository
                    .findByParent_Id(parentIdOfReply, pageable);


            GroupComment parentComment = groupCommentRepository.findById(parentIdOfReply)
                    .orElseThrow(() -> new ResourceNotFoundException("Parent comment not found"));

            CommentResponseDTO parentDTO = mapToDTO(parentComment, currentUser, isAdmin);
            List<CommentResponseDTO> repliesDTO = repliesPage.getContent().stream()
                    .map(reply -> mapToDTO(reply, currentUser, isAdmin))
                    .collect(Collectors.toList());

            // Move focused reply to top
            repliesDTO.sort((a, b) -> {
                if (a.getId().equals(replyToCommentId)) return -1;
                if (b.getId().equals(replyToCommentId)) return 1;
                return 0;
            });

            parentDTO.setReplies(repliesDTO);

            return PaginatedResponse.<CommentResponseDTO>builder()
                    .content(List.of(parentDTO))
                    .pageNumber(repliesPage.getNumber())
                    .pageSize(repliesPage.getSize())
                    .totalElements(repliesPage.getTotalElements())
                    .totalPages(repliesPage.getTotalPages())
                    .build();
        }

        throw new IllegalArgumentException("Invalid request type");
    }

    private CommentResponseDTO mapToDTO(GroupComment comment, User currentUser, boolean isAdmin) {
        return CommentResponseDTO.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .timeAgo(getTimeAgo(comment.getCreatedAt()))
                .likeCount(comment.getLikes().size())
                .isLikedByMe(comment.getLikes().stream()
                        .anyMatch(like -> like.getUser().getId().equals(currentUser.getId())))
                .editAccess(comment.getUser().getId().equals(currentUser.getId()) || isAdmin)
                .deleteAccess(comment.getUser().getId().equals(currentUser.getId()) || isAdmin)
                .user(UserDTO.builder()
                        .id(comment.getUser().getId())
                        .name(comment.getUser().getFirstName() + " " + comment.getUser().getLastName())
                        .username(comment.getUser().getUserName())
                        .avatar(comment.getUser().getProfilePicture())
                        .build())
                .replyCount(comment.getReplies().size())
                .hasMoreReplies(!comment.getReplies().isEmpty())
                .build();
    }

    private String getTimeAgo(Instant dateTime) {
        Duration duration = Duration.between(dateTime, LocalDateTime.now());
        if (duration.toMinutes() < 60)
            return duration.toMinutes() + " minutes ago";
        else if (duration.toHours() < 24)
            return duration.toHours() + " hours ago";
        else
            return duration.toDays() + " days ago";
    }

}
