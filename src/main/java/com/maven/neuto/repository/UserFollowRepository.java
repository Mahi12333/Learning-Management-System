package com.maven.neuto.repository;

import com.maven.neuto.model.UserFollow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserFollowRepository extends JpaRepository<UserFollow, Long> {
    Optional<UserFollow> findByFollowerIdAndFollowingId(Long loggedInUserId, Long id);

    @Transactional
    void deleteByFollowerIdAndFollowingId(Long loggedInUserId, Long id);

    List<UserFollow> findByFollowerIdAndFollowingIdIn(Long currentUserId, List<Long> userIds);
    List<UserFollow> findByFollowerIdInAndFollowingId(List<Long> userIds, Long currentUserId);

    @Query("SELECT COUNT(s) FROM UserFollow s WHERE s.following.id = :userId")
    Integer countByFollowingId(@Param("userId") Long id);

    @Query("SELECT COUNT(s) FROM UserFollow s WHERE s.follower.id = :userId")
    Integer countByFollowerId(@Param("userId") Long id);

    Boolean existsByFollowerIdAndFollowingId(Long currentUserId, Long id);
}
