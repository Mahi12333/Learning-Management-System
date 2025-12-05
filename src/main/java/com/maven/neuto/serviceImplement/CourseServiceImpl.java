package com.maven.neuto.serviceImplement;

import com.maven.neuto.exception.ResourceNotFoundException;
import com.maven.neuto.mapstruct.CourseMapper;
import com.maven.neuto.mapstruct.MapperContext;
import com.maven.neuto.model.Course;
import com.maven.neuto.model.Group;
import com.maven.neuto.payload.request.course.CourseCreateDTO;
import com.maven.neuto.payload.request.course.UpdateCourseDTO;
import com.maven.neuto.payload.response.PaginatedResponse;
import com.maven.neuto.payload.response.course.CourseResponseDTO;
import com.maven.neuto.payload.response.course.PublicCourseResponseDTO;
import com.maven.neuto.payload.response.course.UserCourseResponseDTO;
import com.maven.neuto.payload.response.group.CommentResponseDTO;
import com.maven.neuto.repository.CourseRepository;
import com.maven.neuto.repository.LessonRepository;
import com.maven.neuto.repository.UserRepository;
import com.maven.neuto.service.CourseProjection;
import com.maven.neuto.service.CourseService;
import com.maven.neuto.utils.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final AuthUtil authUtil;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final CourseMapper courseMapper;
    private final MapperContext mapperContext;

    @Override
    public String createCourse(CourseCreateDTO request) {
        Long currentUserId = authUtil.loggedInUserIdForTesting();
        Long communityId = authUtil.communityId();
        Course course = courseMapper.toEntityCreateCourse(request, currentUserId, communityId, mapperContext);
        courseRepository.save(course);
        return "course.created.successfully";
    }

    @Override
    public CourseResponseDTO updateCourse(UpdateCourseDTO request) {
        Long courseId = request.getId();
        Course existingCourse = courseRepository.findById(courseId).orElseThrow(() -> new ResourceNotFoundException("course.not.found"));
        if(Boolean.TRUE.equals(request.getOnlyFetch())){
            return courseMapper.toResponseDto(existingCourse);
        }
        courseMapper.updateCourseFromDto(request, existingCourse);
        if (request.getTags() != null) {
            existingCourse.setTags(request.getTags());
        }
        Course savedGroup = courseRepository.save(existingCourse);
        return courseMapper.toResponseDto(savedGroup);
    }

    @Override
    public PaginatedResponse<PublicCourseResponseDTO> publicCourse(Integer pageNumber, Integer pageSize, String sortOrder) {
        Long communityId = authUtil.communityId();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortOrder).descending());

        // step 1: Fetch page from DB
        Page<CourseProjection> page = courseRepository.findAllPublicCourses(communityId, pageable);

        // step 2: Convert items if needed (optional here because repository already returns DTO)
        List<PublicCourseResponseDTO> dtoList = page.getContent().stream()
                .map(p -> new PublicCourseResponseDTO(
                        p.getId(),                                     // Long id
                        p.getName(),                                   // String name
                        p.getDescription(),                            // String description
                        p.getAbout(),                                  // String about
                        (p.getTags() == null || p.getTags().trim().isEmpty())
                                ? Collections.emptyList()
                                : Arrays.stream(p.getTags().split(","))
                                .map(String::trim)
                                .filter(tag -> !tag.isEmpty())
                                .toList(),                // List<String> tags
                        p.getImagesPath(),                             // String imagesPath
                        false,                                        // boolean archive (default false)
                        p.getName() + "_" + p.getId(),                // String slug
                        p.getTotalModules() != null ? p.getTotalModules() : 0, // int totalModules
                        p.getTotalDuration() != null ? p.getTotalDuration() : 0L, // Long totalVideoDuration
                        p.getTotalLessons() != null ? p.getTotalLessons() : 0,   // int totalLessons
                        null,                                         // Long size
                        0,                                            // int completedLessons
                        0.0,                                          // double progressPercentage
                        p.getCreatedAt(),                             // LocalDateTime createdAt
                        null                                          // LocalDateTime updatedAt
                ))
                .toList();

        // step 3: Build and return paginated response
        return PaginatedResponse.<PublicCourseResponseDTO>builder()
                .content(dtoList)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    @Override
    public PaginatedResponse<PublicCourseResponseDTO> PublicRecommendedCourse(Integer pageNumber, Integer pageSize, String sortOrder) {

        return null;
    }
}
