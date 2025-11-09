package com.maven.neuto.repository;

import com.maven.neuto.emun.ProfileComplete;
import com.maven.neuto.emun.Status;
import com.maven.neuto.model.User;
import com.maven.neuto.payload.response.user.UserDetailsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUserName(String name);
    boolean existsByEmail(String email);
    boolean existsByUserName(String uniqueUserName);
    Optional<Object> findByEmailIgnoreCase(String email);

    //    @Query("SELECT Count(u) FROM User u WHERE u.InvitedBy.id = :userId")
    @Query("SELECT COUNT(u) FROM User u WHERE u.invitedBy.id = :userId")
    Integer findInvitedUserCount(@Param("userId") Long userId);

    @Query("SELECT u FROM User u WHERE u.role.id = :roleId AND u.userCommunity.id = :communityId AND u.profileComplete = :status")
    List<UserDetailsDTO> findUserByRoleAndCommunityAndStatus(@Param("roleId") long roleId, @Param("communityId") Long communityId, @Param("status") ProfileComplete status);

    @Query(" SELECT u FROM User u WHERE u.id <> :userId AND u.role.id <> :roleId AND u.status = :status AND u.profileComplete = :profileComplete AND (:communityId IS NULL OR u.userCommunity.id = :communityId)")
    Page<User> findCommunityUsers(@Param("userId") Long userId, @Param("roleId") Long roleId, @Param("status") Status status, @Param("profileComplete") ProfileComplete profileComplete, @Param("communityId") Long communityId, Pageable pageable);

    @Query("SELECT COUNT(u) FROM User u WHERE u.userCommunity.id = :communityId AND u.role.id = :roleId AND u.profileComplete = :status")
    Long countByRoleAndCommunityIdAndStatus(@Param("communityId") Long communityId,
                                            @Param("roleId") long roleId,
                                            @Param("status") ProfileComplete status);

//    @Query("SELECT u FROM User u WHERE u.userCommunity.id = :communityId AND u.role.id = :roleId")
@Query(
        value = "SELECT u FROM User u WHERE u.userCommunity.id = :communityId AND u.role.id = :roleId",
        countQuery = "SELECT COUNT(u) FROM User u WHERE u.userCommunity.id = :communityId AND u.role.id = :roleId"
)
    Page<User> findAllByCommunityIdAndRoleId(Long communityId, Long roleId, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findUserById(@Param("id") Long id);

    @Query("SELECT u FROM User u WHERE u.userCommunity.id = :communityId AND u.status = :status AND u.profileComplete = :profile AND u.id <> :userId AND u.role.id <> :roleId")
    List<User> findAllByCommunityIdAndStatusAndProfileCompleteAndIdNotAndRoleIdNot(@Param("communityId") Long communityId, @Param("status") Status status, @Param("profile") ProfileComplete complete, @Param("userId") Long currentUserId, @Param("roleId") Long roleId);
}
