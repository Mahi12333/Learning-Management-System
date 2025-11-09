package com.maven.neuto.repository;

import com.maven.neuto.model.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {

    @Query("SELECT DISTINCT lp.lesson.course.id FROM LessonProgress lp WHERE lp.user.id = :userId AND lp.status = 'COMPLETE'")
    List<Long> findDistinctCourseIdsByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(*) FROM LessonProgress lp WHERE lp.user.id = :userId AND lp.lesson.id IN :lessonIds AND lp.status = 'COMPLETE'")
    int countCompletedLessonsByUserIdAndLessonIds(@Param("userId") Long userId,
                                                  @Param("lessonIds") List<Long> lessonIds);
}
