package com.maven.neuto.repository;

import com.maven.neuto.model.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {

    //@Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END FROM Model m WHERE LOWER(m.name) = LOWER(:name) AND m.course.id = :courseId")
    boolean existsByNameIgnoreCaseAndCourseId(@Param("name") String name, @Param("courseId") Long courseId);

    //@Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END FROM Model m WHERE LOWER(m.name) = LOWER(:name) AND m.course.id = :courseId AND m.id <> :moduleId")
    boolean existsByNameIgnoreCaseAndCourseIdAndIdNot(@Param("name") String name, @Param("courseId") Long courseId, @Param("moduleId") Long moduleId);
}
