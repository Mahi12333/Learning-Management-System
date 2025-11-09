package com.maven.neuto.utils;


import com.maven.neuto.model.*;
import com.maven.neuto.payload.response.content.UserShortContentResponseDTO;
import com.maven.neuto.payload.response.course.UserCourseResponseDTO;
import com.maven.neuto.payload.response.group.UserGroupResponseDTO;
import com.maven.neuto.payload.response.user.UserCreatorDTO;
import com.maven.neuto.payload.response.user.UserGroupparticipantsDTO;
import com.maven.neuto.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Component
@RequiredArgsConstructor
public class UserHelperMethod {
     private final CourseRepository courseRepository;
     private final GroupUserRepository groupUserRepository;
     private final ShortContentRepository shortContentRepository;
     private final LessonRepository lessonRepository;
     private final LessonProgressRepository lessonProgressRepository;
     private final UserRepository userRepository;



    public List<UserCourseResponseDTO> findEnrolledCoursesByUserId(Long userId, Pageable pageable) {

        // 1. Get all lesson progress (ongoing courses)
        List<Long> ongoingCourseIds = lessonProgressRepository
                .findDistinctCourseIdsByUserId(userId);

        if (ongoingCourseIds.isEmpty()) return Collections.emptyList();

        // 2. Get paginated course data
        Page<Course> coursesPage = courseRepository.findByIdInAndArchiveFalseAndPrivacyTrue(
                ongoingCourseIds, pageable);

        // 3. Build customized response
        return coursesPage.stream().map(course -> {
            // Flatten lesson data to calculate totals
            List<Lesson> lessons = course.getModules().stream()
                    .flatMap(module -> module.getLessons().stream())
                    .filter(lesson -> !lesson.getArchive())
                    .collect(Collectors.toList());

            int totalLessons = lessons.size();
            Long totalDuration = (long) lessons.stream()
                    .mapToDouble(l -> Optional.ofNullable(l.getVideoDuration()).orElse(0L))
                    .sum();

            // 4. Completed lessons count
            int completedLessons = lessonProgressRepository.countCompletedLessonsByUserIdAndLessonIds(
                    userId, lessons.stream().map(Lesson::getId).collect(Collectors.toList()));

            double progressPercent = totalLessons > 0
                    ? (completedLessons * 100.0 / totalLessons)
                    : 0;

            return UserCourseResponseDTO.builder()
                    .id(course.getId())
                    .name(course.getName())
                    .description(course.getDescription())
                    .about(course.getAbout())
                    .imagesPath(course.getImagesPath())
                    .createdAt(LocalDateTime.ofInstant(course.getCreatedAt(), ZoneOffset.UTC))
                    .updatedAt(LocalDateTime.ofInstant(course.getUpdatedAt(), ZoneOffset.UTC))
                    .slug(course.getSlug())
                    .totalModules(course.getModules().size())
                    .totalVideoDuration(totalDuration)
                    .totalLessons(totalLessons)
                    .completedLessons(completedLessons)
                    .progressPercent(Math.round(progressPercent * 100.0) / 100.0)
                    .build();
        }).collect(Collectors.toList());
    }

    public List<UserCourseResponseDTO> findCoursesCreatedByTeacherId(Long userId, Pageable pageable) {

        Page<Course> coursesPage = courseRepository
                .findByUserIdAndArchiveFalseAndPrivacyTrue(userId, pageable);

        return buildBasicCourseResponse(coursesPage);
    }

    public List<UserCourseResponseDTO> findAllByCommunityId(Long communityId, Pageable pageable) {

        Page<Course> coursesPage = courseRepository
                .findByCommunityIdAndArchiveFalseAndPrivacyTrue(communityId, pageable);

        return buildBasicCourseResponse(coursesPage);
    }

    private List<UserCourseResponseDTO> buildBasicCourseResponse(Page<Course> coursesPage) {
        return coursesPage.stream().map(course -> {
            List<Lesson> lessons = course.getModules().stream()
                    .flatMap(m -> m.getLessons().stream())
                    .filter(l -> !l.getArchive())
                    .collect(Collectors.toList());

            Long totalDuration = (long)lessons.stream()
                    .mapToDouble(l -> Optional.ofNullable(l.getVideoDuration()).orElse(0L))
                    .sum();

            return UserCourseResponseDTO.builder()
                    .id(course.getId())
                    .name(course.getName())
                    .description(course.getDescription())
                    .imagesPath(course.getImagesPath())
                    .createdAt(LocalDateTime.ofInstant(course.getCreatedAt(), ZoneOffset.UTC))
                    .updatedAt(LocalDateTime.ofInstant(course.getUpdatedAt(), ZoneOffset.UTC))
                    .archive(course.getArchive())
                    .totalModules(course.getModules().size())
                    .totalVideoDuration(totalDuration)
                    .slug(course.getSlug())
                    .build();
        }).collect(Collectors.toList());
    }

    public List<UserShortContentResponseDTO> findVisibleToUser(Long userId, Pageable pageable) {
        Page<ShortContentUpload> contentsPage = shortContentRepository.findVisibleContentsForUser(userId, pageable);

        return contentsPage.stream()
                .map(content -> UserShortContentResponseDTO.builder()
                        .id(content.getId())
                        .caption(content.getCaption())
                        .thumbnail(content.getThumbnail())
                        .createdAt(LocalDateTime.ofInstant(content.getCreatedAt(), ZoneOffset.UTC))
                        .updatedAt(LocalDateTime.ofInstant(content.getUpdatedAt(), ZoneOffset.UTC))
                        .isBookmark(content.getBookmarkShortContent().stream()
                                .anyMatch(b -> b.getBookmarkUser().getId().equals(userId)))
                        .isLike(content.getBookmarkShortContent().stream()
                                .anyMatch(l -> l.getBookmarkUser().getId().equals(userId)))
                        .build()
                )
                .collect(Collectors.toList());
    }

    public List<UserGroupResponseDTO> findGroupsByUserId(Long userId, Pageable pageable) {
        Page<Group> groupsPage = groupUserRepository.findGroupsByUserId(userId, pageable);
        return buildGroupResponse(groupsPage, userId);
    }

    public List<UserGroupResponseDTO> findGroupsCreatedByUser(Long creatorId, Pageable pageable) {
        Page<Group> groupsPage = groupUserRepository.findGroupsCreatedByUser(creatorId, pageable);
        return buildGroupResponse(groupsPage, creatorId);
    }

    public List<UserGroupResponseDTO> findAllByCommunityIdForGroup(Long communityId, Pageable pageable) {
        Page<Group> groupPage = groupUserRepository.findByCommunityIdAndArchiveFalseAndPrivacyTrue(communityId, pageable);
        return buildGroupResponse(groupPage, null);
    }

    private List<UserGroupResponseDTO> buildGroupResponse(Page<Group> groupPage, Long viewerId) {

        return groupPage.stream().map(group -> {

            // 1️⃣ Fetch creator
            User creator = userRepository.findById(group.getCreatedBy().getId()).orElse(null);
            Role role = creator != null ? creator.getRole() : null;

            // 2️⃣ Fetch group members manually
            List<GroupUser> groupUsers = groupUserRepository.findByGroup(group.getId());

            // 3️⃣ Fetch each user using userId
            List<UserGroupparticipantsDTO> members = groupUsers.stream()
                    .map(gu -> {
                        User u = userRepository.findById(gu.getUserId()).orElse(null);
                        if (u == null) return null;
                        return new UserGroupparticipantsDTO(
                                u.getId(),
                                u.getFirstName(),
                                u.getLastName(),
                                u.getUserName(),
                                u.getProfilePicture(),
                                u.getEmail()
                        );
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            // 4️⃣ Check viewer is member
            boolean isViewerInGroup = viewerId != null &&
                    members.stream().anyMatch(m -> m.getId().equals(viewerId));

            // 5️⃣ Build final response DTO
            return UserGroupResponseDTO.builder()
                    .id(group.getId())
                    .name(group.getName())
                    .description(group.getDescription())
                    .createdDate(group.getCreatedAt())
                    .privacy(group.getPrivacy())
                    .createdBy(UserCreatorDTO.builder()
                            .firstName(creator != null ? creator.getFirstName() : null)
                            .lastName(creator != null ? creator.getLastName() : null)
                            .userName(creator != null ? creator.getUserName() : null)
                            .profilePicture(creator != null ? creator.getProfilePicture() : null)
                            .role(role != null ? role.getName() : null)
                            .build())
                    .users(members)
                    .viewAccess(isViewerInGroup || Objects.equals(viewerId, creator != null ? creator.getId() : null))
                    .build();

        }).collect(Collectors.toList());
    }

    public List<UserShortContentResponseDTO> findVisibleToHisContent(Long id, Pageable pageable) {
        Page<ShortContentUpload> contentsPage = shortContentRepository.findVisibleContentsForUser(id, pageable);

        return contentsPage.stream()
                .map(content -> UserShortContentResponseDTO.builder()
                        .id(content.getId())
                        .caption(content.getCaption())
                        .thumbnail(content.getThumbnail())
                        .createdAt(LocalDateTime.ofInstant(content.getCreatedAt(), ZoneOffset.UTC))
                        .updatedAt(LocalDateTime.ofInstant(content.getUpdatedAt(), ZoneOffset.UTC))
                        .isBookmark(false)
                        .isLike(false)
                        .build()
                )
                .collect(Collectors.toList());
    }
}
