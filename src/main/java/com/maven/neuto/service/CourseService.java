package com.maven.neuto.service;


import com.maven.neuto.payload.request.course.CourseCreateDTO;
import com.maven.neuto.payload.request.course.UpdateCourseDTO;
import jakarta.validation.Valid;

public interface CourseService {
    String createCourse(CourseCreateDTO request);
    String updateCourse(UpdateCourseDTO request);
}
