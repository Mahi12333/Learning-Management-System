package com.maven.neuto.mapstruct;

import com.maven.neuto.model.Course;
import com.maven.neuto.model.Lesson;
import com.maven.neuto.model.Module;
import com.maven.neuto.payload.request.course.*;
import com.maven.neuto.payload.response.course.CourseResponseDTO;
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

    @Mapping(target = "course.id", source = "request.courseId")
    Module toEntityCreateModule(ModuleCreateDTO request, @Context MapperContext mapperContext);

    @Mapping(target = "lessonCreator.id", source = "currentUserId")
    @Mapping(target = "module.id", source = "request.moduleId")
    @Mapping(target = "course.id", source = "courseId")
    @Mapping(target = "imagesPath", source = "request.path")
    Lesson toEntityCreateLesson(LessonCreateDTO request, Long currentUserId, Long courseId, @Context MapperContext mapperContext);

    @Mapping(target = "lessonCreator.id", source = "currentUserId")
    @Mapping(target = "module.id", source = "moduleId")
    @Mapping(target = "course.id", source = "courseId")
    Lesson toEntityUpdateLesson(LessonUpdateDTO request, Long currentUserId, Long moduleId, Long courseId, @Context MapperContext mapperContext);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Module updateModuleFromDto(ModuleUpdateDTO request,  @MappingTarget Module existingModule);


//    void toEntityCreateModule(ModuleCreateDTO request, Long currentUserId, Long communityId, MapperContext mapperContext);
}
