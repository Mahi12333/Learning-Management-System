package com.maven.neuto.repository;

import com.maven.neuto.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    @Query("SELECT COUNT(l) FROM Lesson l WHERE l.lessonCommunity.id = :communityId")
    Long countByCommunityId(Long communityId);
    @Query("SELECT SUM(l.size) FROM Lesson l WHERE l.lessonCommunity.id = :communityId")
    Long sumSizeByCommunityId(@Param("communityId") Long communityId);
}
