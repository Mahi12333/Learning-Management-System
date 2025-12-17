package com.maven.neuto.serviceImplement;

import com.maven.neuto.exception.APIException;
import com.maven.neuto.exception.ResourceNotFoundException;
import com.maven.neuto.mapstruct.CourseMapper;
import com.maven.neuto.mapstruct.MapperContext;
import com.maven.neuto.model.Course;
import com.maven.neuto.model.Lesson;
import com.maven.neuto.model.User;
import com.maven.neuto.model.Module;
import com.maven.neuto.payload.request.course.*;
import com.maven.neuto.payload.response.PaginatedResponse;
import com.maven.neuto.payload.response.course.CourseResponseDTO;
import com.maven.neuto.payload.response.course.PublicCourseResponseDTO;
import com.maven.neuto.repository.CourseRepository;
import com.maven.neuto.repository.LessonRepository;
import com.maven.neuto.repository.ModuleRepository;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
    private final ModuleRepository moduleRepository;

    @Override
    public String createCourse(CourseCreateDTO request) {
        Long currentUserId = authUtil.loggedInUserIdForTesting();
        Long communityId = authUtil.communityId();
        Course existingCourse = courseRepository.findAll().stream()
                .filter(c -> c.getName().equalsIgnoreCase(request.getName())
                        && c.getCourseCommunity().getId().equals(communityId))
                .findFirst()
                .orElse(null);
        if (existingCourse != null) {
            throw new APIException("course.already.exists", HttpStatus.BAD_REQUEST);
        }
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
        Course courseWithSameName = courseRepository.findAll().stream()
                .filter(c -> c.getName().equalsIgnoreCase(request.getName())
                        && c.getCourseCommunity().getId().equals(existingCourse.getCourseCommunity().getId())
                        && !c.getId().equals(courseId))
                .findFirst()
                .orElse(null);
        if (courseWithSameName != null) {
            throw new APIException("course.name.already.exists", HttpStatus.BAD_REQUEST);
        }
        courseMapper.updateCourseFromDto(request, existingCourse);
        if (request.getTags() != null) {
            existingCourse.setTags(request.getTags());
        }
        Course savedGroup = courseRepository.save(existingCourse);
        return courseMapper.toResponseDto(savedGroup);
    }

    @Override
    public PaginatedResponse<PublicCourseResponseDTO> publicCourse(Integer pageNumber, Integer pageSize, String sortOrder, String sortBy) {
        Long communityId = authUtil.communityId();
        Sort sortByOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        int page = Math.max(pageNumber - 1, 0);
        Pageable pageable = PageRequest.of(page, pageSize, sortByOrder);

        // step 1: Fetch page from DB
        Page<CourseProjection> pageResult = courseRepository.findAllPublicCourses(communityId, pageable);
        log.info("page content size: {}", pageResult.getContent().size());
        log.info("page content: {}", pageResult);

        // step 2: Convert items if needed (optional here because repository already returns DTO)
        List<PublicCourseResponseDTO> dtoList = pageResult.getContent().stream()
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
                        p.getImagesPath(),
                        p.getArchive(),                                        // boolean archive (default false)
                        p.getName() + "_" + p.getId(),                // String slug
                        p.getTotalModules() != null ? p.getTotalModules() : 0L, // int totalModules
                        p.getTotalDuration() != null ? p.getTotalDuration() : 0L, // Long totalVideoDuration
                        p.getTotalLessons() != null ? p.getTotalLessons() : 0L,   // int totalLessons
                        p.getSize(),                                         // Long size
                        0,                                            // int completedLessons
                        0.0,                                          // double progressPercentage
                        p.getCreatedAt(),                             // LocalDateTime createdAt
                        p.getUpdatedAt()
                ))
                .toList();

        // step 3: Build and return paginated response
        return PaginatedResponse.<PublicCourseResponseDTO>builder()
                .content(dtoList)
                .pageNumber(pageResult.getNumber())
                .pageSize(pageResult.getSize())
                .totalElements(pageResult.getTotalElements())
                .totalPages(pageResult.getTotalPages())
                .build();
    }

    @Override
    public PaginatedResponse<PublicCourseResponseDTO> PublicRecommendedCourse(Integer pageNumber,
                                                                              Integer pageSize,
                                                                              String sortOrder, String sortBy) {
        Long currentUserId = authUtil.loggedInUserIdForTesting();
        Long communityId = authUtil.communityId();

        // 1\) load user and get interests as List\<String\>
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("user.not.found"));

        List<String> interests = user.getInterest(); // e.g. ["java","sql"]
        if (interests == null || interests.isEmpty()) {
            return PaginatedResponse.<PublicCourseResponseDTO>builder()
                    .content(Collections.emptyList())
                    .pageNumber(pageNumber)
                    .pageSize(pageSize)
                    .totalElements(0)
                    .totalPages(0)
                    .build();
        }

        // 2\) normalize interests to lower\-case set
        List<String> normalizedInterests = interests.stream()
                .filter(i -> i != null && !i.trim().isEmpty())
                .map(String::trim)
                .map(String::toLowerCase)
                .toList();
        log.info("normalized interests: {}", normalizedInterests);
        if (normalizedInterests.isEmpty()) {
            return PaginatedResponse.<PublicCourseResponseDTO>builder()
                    .content(Collections.emptyList())
                    .pageNumber(pageNumber)
                    .pageSize(pageSize)
                    .totalElements(0)
                    .totalPages(0)
                    .build();
        }
        Sort sortByOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        int page = Math.max(pageNumber - 1, 0);
        Pageable pageable = PageRequest.of(page, pageSize, sortByOrder);
        // step 1: Fetch page from DB
        Page<CourseProjection> pageResult = courseRepository.findAllPublicCourses(communityId, pageable);
        // 4) filter courses whose tags overlap with interests
        List<CourseProjection> matchedCourses = pageResult.stream()
                .filter(p -> {
                    String tagsStr = p.getTags(); // e.g. "Javascript, sql"
                    if (tagsStr == null || tagsStr.trim().isEmpty()) {
                        return false;
                    }
                    List<String> courseTags = Arrays.stream(tagsStr.split(","))
                            .map(String::trim)
                            .filter(t -> !t.isEmpty())
                            .map(String::toLowerCase)
                            .toList();

                    // any overlap between interests and courseTags
                    return courseTags.stream().anyMatch(normalizedInterests::contains);
                })
                .toList();

        int totalElements = matchedCourses.size();

        List<PublicCourseResponseDTO> dtoList = matchedCourses.stream()
                .map(p -> new PublicCourseResponseDTO(
                        p.getId(),
                        p.getName(),
                        p.getDescription(),
                        p.getAbout(),
                        (p.getTags() == null || p.getTags().trim().isEmpty())
                                ? Collections.emptyList()
                                : Arrays.stream(p.getTags().split(","))
                                .map(String::trim)
                                .filter(tag -> !tag.isEmpty())
                                .toList(),
                        p.getImagesPath(),
                        false,
                        p.getName() + "_" + p.getId(),
                        p.getTotalModules() != null ? p.getTotalModules() : 0,
                        p.getTotalDuration() != null ? p.getTotalDuration() : 0L,
                        p.getTotalLessons() != null ? p.getTotalLessons() : 0,
                        null,
                        0,
                        0.0,
                        p.getCreatedAt(),
                        null
                ))
                .toList();

        int totalPages = (int) Math.ceil((double) totalElements / pageSize);

        return PaginatedResponse.<PublicCourseResponseDTO>builder()
                .content(dtoList)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .build();
    }
    @Override
    public String createModule(ModuleCreateDTO request) {
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("course.not.found"));
        boolean moduleExists = moduleRepository.existsByNameIgnoreCaseAndCourseId(
                        request.getName(),
                        course.getId()
                );

        if (moduleExists) {
            throw new APIException("module.already.exists", HttpStatus.BAD_REQUEST);
        }

        Module modules = courseMapper.toEntityCreateModule(request, mapperContext);
        moduleRepository.save(modules);

        return "module.created.successfully";
    }

    @Override
    public String updateModule(ModuleUpdateDTO request) {
        Long moduleId = request.getModuleId();
        Module existingModule = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("module.not.found"));

        Long courseId = request.getCourseId();
        boolean exists = courseRepository.existsById(courseId);
        if (!exists) {
            throw new APIException("course.not.found", HttpStatus.BAD_REQUEST);
        }
        boolean moduleWithSameNameExists =
                moduleRepository.existsByNameIgnoreCaseAndCourseIdAndIdNot(
                        request.getName(),
                        courseId,
                        moduleId
                );
        if (moduleWithSameNameExists) {
            throw new APIException("module.name.already.exists", HttpStatus.BAD_REQUEST);
        }

        Module updateModule = courseMapper.updateModuleFromDto(request, existingModule);
        moduleRepository.save(updateModule);
        return "module.updated.successfully";
    }

    @Override
    public String createLesson(LessonCreateDTO request) {
        Long currentUserId = authUtil.loggedInUserIdForTesting();
        Module ModuleExists = moduleRepository.findById(request.getModuleId()).orElseThrow(() -> new ResourceNotFoundException("module.not.found"));
        if (ModuleExists == null) {
            throw new APIException("module.not.found", HttpStatus.BAD_REQUEST);
        }
        Long courseId = ModuleExists.getCourse().getId();
        Lesson lesson = courseMapper.toEntityCreateLesson(request, currentUserId, courseId, mapperContext);
        lessonRepository.save(lesson);
        return "lesson.created.successfully";
    }

    @Override
    public String updateLesson(LessonUpdateDTO request) {
        Long currentUserId = authUtil.loggedInUserIdForTesting();
        // id will be split from lessonSlug ex-- lesson name = "introduction-to-java_15" , id=15
        Long lessonId = Long.parseLong(request.getLessonSlug().substring(request.getLessonSlug().lastIndexOf("_") + 1));
        Optional<Lesson> LessonExists = lessonRepository.findById(lessonId);
        if (LessonExists == null) {
            throw new APIException("lesson.not.found", HttpStatus.BAD_REQUEST);
        }
        Long moduleId = LessonExists.get().getModule().getId();
        Long courseId = LessonExists.get().getCourse().getId();
        Lesson lesson = courseMapper.toEntityUpdateLesson(request, currentUserId, moduleId, courseId, mapperContext);
        lessonRepository.save(lesson);
        return "lesson.updated.successfully";
    }


}
