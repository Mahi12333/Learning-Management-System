package com.maven.neuto.repository;

import com.maven.neuto.model.Group;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    boolean existsByName(String name);
    @Query("SELECT COUNT(g) FROM Group g WHERE g.community.id = :communityId")
    Long countByCommunityId(Long communityId);
    @Query("SELECT SUM(g.size) FROM Group g WHERE g.community.id = :communityId")
    Long sumSizeByCommunityId(@Param("communityId") Long communityId);

    @Modifying
    @Transactional
    @Query("UPDATE Group g SET g.name = :name WHERE g.id = :id")
    int updateGroup(@Param("id") Long id, @Param("name") String name);

}
