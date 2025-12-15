package com.maven.neuto.mapstruct;

import com.maven.neuto.model.Course;
import com.maven.neuto.model.Group;
import com.maven.neuto.payload.request.course.CourseCreateDTO;
import com.maven.neuto.payload.request.course.UpdateCourseDTO;
import com.maven.neuto.payload.response.course.CourseResponseDTO;
import com.maven.neuto.payload.response.group.GroupResponseDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    @Mapping(target = "courseCreator.id", source = "CreatedById")
    @Mapping(target = "courseCommunity.id", source = "communityId")
    @Mapping(target = "imagesPath", source = "request.path")
    Course toEntityCreateCourse(CourseCreateDTO request, Long CreatedById, Long communityId,  @Context MapperContext courseContext);

    // Entity → Response DTO (for returning )
    CourseResponseDTO toResponseDto(Course entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCourseFromDto(UpdateCourseDTO request, @MappingTarget Course existingCourse);
}
