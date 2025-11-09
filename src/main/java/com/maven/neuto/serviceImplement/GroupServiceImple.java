package com.maven.neuto.serviceImplement;

import com.google.gson.Gson;
import com.maven.neuto.emun.GroupStatus;
import com.maven.neuto.exception.APIException;
import com.maven.neuto.exception.ResourceNotFoundException;
import com.maven.neuto.mapstruct.GroupMapper;
import com.maven.neuto.mapstruct.MapperContext;
import com.maven.neuto.model.Community;
import com.maven.neuto.model.Group;
import com.maven.neuto.model.GroupUser;
import com.maven.neuto.model.User;
import com.maven.neuto.payload.request.group.CreateGroupComment;
import com.maven.neuto.payload.request.group.GroupCreateDTO;
import com.maven.neuto.payload.request.group.GroupUpdateDTO;
import com.maven.neuto.payload.response.group.GroupCommentResponseDTO;
import com.maven.neuto.payload.response.group.GroupResponseDTO;
import com.maven.neuto.repository.CommunityRepository;
import com.maven.neuto.repository.GroupRepository;
import com.maven.neuto.repository.GroupUserRepository;
import com.maven.neuto.repository.UserRepository;
import com.maven.neuto.service.GroupService;
import com.maven.neuto.utils.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

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

    @Override
    public GroupResponseDTO createGroup(GroupCreateDTO request) {
        Long currentUserId = authUtil.loggedInUserId();
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
        if(groupId == null){
            throw new APIException("Group id is required", HttpStatus.BAD_REQUEST);
        }
        Group existingGroup = groupRepository.findById(groupId).orElseThrow(()-> new ResourceNotFoundException("Group not found"));
        if(Boolean.TRUE.equals(request.getOnlyFetch())){
            return groupMapper.toResponseDto(existingGroup);
        }
        groupMapper.updateGroupFromDto(request, existingGroup);
        if (request.getTags() != null) {
            existingGroup.setTags(request.getTags());
        }
        Group savedGroup = groupRepository.save(existingGroup);
        return groupMapper.toResponseDto(savedGroup);
    }

    @Override
    public void deleteGroup(Long groupId) {
        Group existingGroup = groupRepository.findById(groupId).orElseThrow(()-> new ResourceNotFoundException("Group not found"));
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
        return null;
    }
}
