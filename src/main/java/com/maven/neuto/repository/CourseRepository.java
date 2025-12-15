package com.maven.neuto.repository;

import com.maven.neuto.model.Course;
import com.maven.neuto.payload.response.course.PublicCourseResponseDTO;
import com.maven.neuto.payload.response.course.UserCourseResponseDTO;
import com.maven.neuto.service.CourseProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @Query(
            value = """
        SELECT
              c.id AS id,
              c.name AS name,
              c.tags AS tags,
              c.imagesPath AS imagesPath,
              c.size AS size,
              c.archive AS archive,
              c.createdAt AS createdAt,
              c.updatedAt AS updatedAt,
              COUNT(DISTINCT m.id) AS totalModules,
              COUNT(DISTINCT l.id) AS totalLessons,
              COALESCE(SUM(l.videoDuration), 0) AS totalDuration
          FROM Course c
          LEFT JOIN c.modules m
                 ON m.archive = false
          LEFT JOIN m.lessons l
                 ON l.archive = false
          WHERE c.archive = false
            AND c.courseCommunity.id = :communityId
          GROUP BY
              c.id,
              c.name,
              c.tags,
              c.imagesPath,
              c.size,
              c.archive,
              c.createdAt,
              c.updatedAt,
        """,
            countQuery = """
        SELECT COUNT(c.id)
        FROM Course c
        WHERE c.archive = false
          AND c.courseCommunity.id = :communityId
        """
    )
    Page<CourseProjection> findAllPublicCourses(
            @Param("communityId") Long communityId,
            Pageable pageable
    );



}
