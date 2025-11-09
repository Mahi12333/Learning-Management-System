package com.maven.neuto.serviceImplement;

import com.maven.neuto.model.User;
import com.maven.neuto.emun.ProfileComplete;
import com.maven.neuto.emun.Status;
import com.maven.neuto.exception.ResourceNotFoundException;
import com.maven.neuto.mapstruct.CommunityMapper;
import com.maven.neuto.model.*;
import com.maven.neuto.payload.request.community.CommunityUserDTO;
import com.maven.neuto.payload.request.community.CreateCommunityDTO;
import com.maven.neuto.payload.request.community.UpdatedCommunityDTO;
import com.maven.neuto.payload.response.PaginatedResponse;
import com.maven.neuto.payload.response.community.CommunityResponseDTO;
import com.maven.neuto.payload.response.community.SuperAdminCommunityPageResponseDTO;
import com.maven.neuto.payload.response.user.UserResponseDTO;
import com.maven.neuto.repository.*;
import com.maven.neuto.service.CommunityService;
import com.maven.neuto.utils.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityServiceImple implements CommunityService {
    private final AuthUtil authUtil;
    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;
    private final CommunityMapper communityMapper;
    private final IndustryRepository industryRepository;
    private final UserFollowRepository userFollowRepository;
    private final CourseRepository courseRepository;
    private final ShortContentRepository shortContentRepository;
    private final BannerRepository bannerRepository;
    private final ModuleTaskRepository moduleTaskRepository;
    private final LessonRepository lessonRepository;
    private final GroupRepository groupRepository;


    @Override
    public CommunityResponseDTO createCommunity(CreateCommunityDTO request) {
        Long currentUserId = authUtil.loggedInUserIdForTesting();
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("user.notfound"));
        Industry industry = industryRepository.findById(request.getIndustryId())
                .orElseThrow(() -> new ResourceNotFoundException("Industry.not.found"));

        // Map DTO → Entity
        Community community = communityMapper.toEntityCreateCommunity(request);
        community.setCommunityCreatedUser(user);
        community.setIndustry(industry);
        community.setCreatedAt(Instant.now());
        community.setUpdatedAt(Instant.now());

        Community savedCommunity = communityRepository.save(community);
        user.setUserCommunity(savedCommunity);
        user.setCommunityComplete(ProfileComplete.COMPLETE);
        userRepository.save(user);
        // Map Entity → ResponseDTO
        return communityMapper.toDtoResponseCommunity(savedCommunity);
    }

    @Override
    public void deleteCommunity(Long communityId) {
        Community community = communityRepository.findById(communityId).orElseThrow(()-> new ResourceNotFoundException("Community not found"));
        communityRepository.delete(community);
    }

    @Override
    public CommunityResponseDTO updatedCommunity(UpdatedCommunityDTO request) {
        Long currentUserId = authUtil.loggedInUserId();
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Community community = communityRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Community not found"));
        community.setCommunityCreatedUser(user);
        if(request.getIndustryId() != null){
            Industry industry = industryRepository.findById(request.getIndustryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Industry not found"));
            community.setIndustry(industry);
        }

        // Map other fields (only non-null are applied)
        communityMapper.updateCommunityFromDto(request, community);

        // Save entity
        Community updated = communityRepository.save(community);
        return communityMapper.toDtoResponseCommunity(updated);
    }

    @Override
    public PaginatedResponse<CommunityUserDTO> getUserCommunity(Integer pageNumber, Integer pageSize, String sortOrder) {
        Long currentUserId = authUtil.loggedInUserId();
        User existingUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortOrder.equalsIgnoreCase("desc") ? Sort.by("id").descending() : Sort.by("id").ascending());
        Page<User> pageResult = userRepository.findCommunityUsers(currentUserId, 1L, Status.ACTIVE, ProfileComplete.COMPLETE, existingUser.getUserCommunity() != null ? existingUser.getUserCommunity().getId() : null, pageable);
        if(pageResult.isEmpty()){
            return new PaginatedResponse<>(Collections.emptyList(), pageNumber, pageSize, 0, 0);
        }
        List<Long> userIds = pageResult.stream().map(User:: getId).toList();
        // Fetch follow info
        List<UserFollow> followings = userFollowRepository.findByFollowerIdAndFollowingIdIn(currentUserId, userIds);
        List<UserFollow> followers = userFollowRepository.findByFollowerIdInAndFollowingId(userIds, currentUserId);

        Set<Long> followingSet = followings.stream().map(f -> f.getFollowing().getId()).collect(Collectors.toSet());
        Set<Long> followerSet = followers.stream().map(f -> f.getFollower().getId()).collect(Collectors.toSet());

        List<CommunityUserDTO> enrichedUsers = pageResult.stream().map(user -> {
          return   CommunityUserDTO.builder()
            .id(user.getId())
            .firstName(user.getFirstName())
            .userName(user.getUserName())
            .lastName(user.getLastName())
                    .profileImage(user.getProfilePicture())
                    .isFollowing(followingSet.contains(user.getId()))
            .isFollower(followerSet.contains(user.getId()))
                    .build();
        }).toList();
        return new PaginatedResponse<>(enrichedUsers, pageNumber, pageSize, pageResult.getTotalElements(), pageResult.getTotalPages());
    }

    @Override
    public SuperAdminCommunityPageResponseDTO communityPageSuperAdmin(Long communityId, String userType, Integer pageNumber, Integer pageSize) {
        Community communityInfo = communityRepository.findById(communityId).orElseThrow(()-> new ResourceNotFoundException("Community not found"));
        CommunityResponseDTO communityResponseDTO = CommunityResponseDTO.builder()
                .id(communityInfo.getId())
                .name(communityInfo.getName())
                .tagline(communityInfo.getTagline())
                .imagesPath(communityInfo.getImagesPath())
                .createdAt(LocalDateTime.ofInstant(communityInfo.getCreatedAt(), ZoneOffset.UTC))
                .build();
        Long teacherUserCount = userRepository.countByRoleAndCommunityIdAndStatus(communityId, 3L, ProfileComplete.COMPLETE);
        Long memberUserCount = userRepository.countByRoleAndCommunityIdAndStatus(communityId, 4L, ProfileComplete.COMPLETE);
        // Pagination will be apply on this
        Long roleId;
        switch (userType.toLowerCase()) {
            case "admin" -> roleId = 2L;
            case "teacher" -> roleId = 3L;
            case "member" -> roleId = 4L;
            default -> throw new IllegalArgumentException("Invalid userType: "+ userType);
        }

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdAt").descending());
        Page<User> usersPage = userRepository.findAllByCommunityIdAndRoleId(communityId, roleId, pageable);

        List<UserResponseDTO> userDTOList = usersPage.getContent().stream()
                .map(u -> UserResponseDTO.builder()
                        .id(u.getId())
                        .name(u.getUserName())
                        .email(u.getEmail())
                        .role(u.getRole().getName())
                        .createdAt(LocalDateTime.ofInstant(u.getCreatedAt(), ZoneOffset.UTC))
                        .build())
                .toList();

        PaginatedResponse<UserResponseDTO> usersPaginated = PaginatedResponse.<UserResponseDTO>builder()
                .content(userDTOList)
                .pageNumber(usersPage.getNumber())
                .pageSize(usersPage.getSize())
                .totalElements(usersPage.getTotalElements())
                .totalPages(usersPage.getTotalPages())
                .build();

        // ✅ Counts and storage sizes
        Long courseCount = courseRepository.countByCommunityId(communityId);
        Long lessonCount = lessonRepository.countByCommunityId(communityId);
        Long groupCount = groupRepository.countByCommunityId(communityId);
        Long shortContentCount = shortContentRepository.countByCommunityId(communityId);
        Long bannerCount = bannerRepository.countByCommunityId(communityId);

        Long totalSize = 0L;
        totalSize += courseRepository.sumSizeByCommunityId(communityId);
        totalSize += lessonRepository.sumSizeByCommunityId(communityId);
        totalSize += groupRepository.sumSizeByCommunityId(communityId);
        totalSize += shortContentRepository.sumSizeByCommunityId(communityId);
        totalSize += bannerRepository.sumSizeByCommunityId(communityId);

        // ✅ Build response DTO
        return SuperAdminCommunityPageResponseDTO.builder()
                .communityResponseDTO(communityResponseDTO)
                .totalTeachers(teacherUserCount)
                .totalMembers(memberUserCount)
                .totalCourses(courseCount)
                .totalLessons(lessonCount)
                .totalGroups(groupCount)
                .totalShortContents(shortContentCount)
                .totalBanners(bannerCount)
                .totalStorageSize(totalSize)
                .users(usersPaginated)
                .build();
    }


}
