package com.maven.neuto.repository;

import com.maven.neuto.model.Comment;
import com.maven.neuto.model.Community;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long> {
    @Query("SELECT c FROM Community c WHERE c.communityCreatedUser.id = :userId")
    Community findByUserId(@Param("userId") Long userId);
}
