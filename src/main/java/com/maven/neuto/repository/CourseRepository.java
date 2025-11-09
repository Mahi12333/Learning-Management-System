package com.maven.neuto.repository;

import com.maven.neuto.model.Course;
import com.maven.neuto.payload.response.course.UserCourseResponseDTO;
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
}
