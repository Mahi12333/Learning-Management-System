package com.maven.neuto.repository;

import com.maven.neuto.model.Group;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    boolean existsByName(String name);
    @Query("SELECT COUNT(g) FROM Group g WHERE g.community.id = :communityId")
    Long countByCommunityId(Long communityId);
    @Query("SELECT SUM(g.size) FROM Group g WHERE g.community.id = :communityId")
    Long sumSizeByCommunityId(@Param("communityId") Long communityId);
}
