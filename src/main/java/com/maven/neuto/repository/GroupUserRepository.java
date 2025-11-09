package com.maven.neuto.repository;

import com.maven.neuto.model.Group;
import com.maven.neuto.model.GroupUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupUserRepository extends JpaRepository<GroupUser, Long> {
    @Query("SELECT gu FROM GroupUser gu WHERE gu.userId = :currentUserId AND gu.group = :groupId")
    Optional<GroupUser> findByUserIdAndGroupId(@Param("currentUserId") Long currentUserId, @Param("groupId") Long groupId);

    @Query("SELECT g FROM Group g WHERE g.id IN ( SELECT gu.group FROM GroupUser gu WHERE gu.userId = :userId ) AND g.privacy = true")
    Page<Group> findGroupsByUserId(@Param("groupId") Long id, Pageable pageable);

    @Query("SELECT g FROM Group g WHERE g.createdBy.id = :id AND g.privacy = true")
    Page<Group> findGroupsCreatedByUser(@Param("id") Long id, Pageable pageable);

    @Query("SELECT g FROM Group g WHERE g.community.id = :communityId AND g.privacy = true")
    Page<Group> findByCommunityIdAndArchiveFalseAndPrivacyTrue(@Param("communityId") Long communityId, Pageable pageable);

    @Query("SELECT gu FROM GroupUser gu WHERE gu.group = :groupId")
    List<GroupUser> findByGroup(@Param("groupId") Long id);
}
