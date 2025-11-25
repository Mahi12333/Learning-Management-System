package com.maven.neuto.serviceImplement;

import com.maven.neuto.payload.request.course.CourseCreateDTO;
import com.maven.neuto.payload.request.course.UpdateCourseDTO;
import com.maven.neuto.repository.CourseRepository;
import com.maven.neuto.repository.LessonRepository;
import com.maven.neuto.repository.UserRepository;
import com.maven.neuto.service.CourseService;
import com.maven.neuto.utils.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final AuthUtil authUtil;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;

    @Override
    public String createCourse(CourseCreateDTO request) {
        Long currentUserId = authUtil.loggedInUserIdForTesting();

        return "";
    }

    @Override
    public String updateCourse(UpdateCourseDTO request) {
        return "";
    }
}
