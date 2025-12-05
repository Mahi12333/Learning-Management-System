package com.maven.neuto.repository;

import com.maven.neuto.model.Course;
import com.maven.neuto.payload.response.course.PublicCourseResponseDTO;
import com.maven.neuto.payload.response.course.UserCourseResponseDTO;
import com.maven.neuto.service.CourseProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    @Query("SELECT COUNT(c) FROM Course c WHERE c.courseCommunity.id = :communityId")
    Long countByCommunityId(@Param("communityId") Long communityId);
    @Query("SELECT SUM(c.size) FROM Course c WHERE c.courseCommunity.id = :communityId")
    Long sumSizeByCommunityId(@Param("communityId") Long communityId);


    @Query("SELECT c FROM Course c WHERE c.id IN :ongoingCourseIds AND c.archive = false")
    Page<Course> findByIdInAndArchiveFalseAndPrivacyTrue(@Param("ongoingCourseIds") List<Long> ongoingCourseIds, Pageable pageable);

    @Query("SELECT c FROM Course c WHERE c.courseCreator.id = :userId AND c.archive = false")
    Page<Course> findByUserIdAndArchiveFalseAndPrivacyTrue(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT c FROM Course c WHERE c.courseCommunity.id = :communityId AND c.archive = false")
    Page<Course> findByCommunityIdAndArchiveFalseAndPrivacyTrue(@Param("communityId") Long communityId, Pageable pageable);

    @Query("""
            SELECT
                  c.id AS id,
                  c.name AS name,
                  c.description AS description,
                  c.about AS about,
                  c.tags AS tags,
                  c.imagesPath AS imagesPath,
                  c.createdAt AS createdAt,
                  COUNT(DISTINCT m.id) AS totalModules,
                  COUNT(l.id) AS totalLessons,
                  COALESCE(SUM(l.videoDuration), 0) AS totalDuration
              FROM Course c
              LEFT JOIN c.modules m ON m.archive = false AND m.courseCommunity.id = :communityId
              LEFT JOIN m.lessons l ON l.archive = false AND l.courseCommunity.id = :communityId
              WHERE c.archive = false AND c.courseCommunity.id = :communityId
              GROUP BY c.id""")
    Page<CourseProjection> findAllPublicCourses(Long communityId, Pageable pageable);
}
