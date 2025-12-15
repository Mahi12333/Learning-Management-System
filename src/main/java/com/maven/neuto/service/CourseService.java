package com.maven.neuto.service;


import com.maven.neuto.payload.request.course.CourseCreateDTO;
import com.maven.neuto.payload.request.course.UpdateCourseDTO;
import com.maven.neuto.payload.response.PaginatedResponse;
import com.maven.neuto.payload.response.course.CourseResponseDTO;
import com.maven.neuto.payload.response.course.PublicCourseResponseDTO;
import jakarta.validation.Valid;

public interface CourseService {
    String createCourse(CourseCreateDTO request);
    CourseResponseDTO updateCourse(UpdateCourseDTO request);

    PaginatedResponse<PublicCourseResponseDTO> publicCourse(Integer pageNumber, Integer pageSize, String sortOrder, String sortBy);

    PaginatedResponse<PublicCourseResponseDTO> PublicRecommendedCourse(Integer pageNumber, Integer pageSize, String sortOrder, String sortBy);
}
