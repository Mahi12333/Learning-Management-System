package com.maven.neuto.repository;

import com.maven.neuto.model.ChatBlockedUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlockedUserRepository extends JpaRepository<ChatBlockedUser, Long> {

    @Query("""
        SELECT CASE WHEN COUNT(bu) > 0 THEN true ELSE false END
        FROM ChatBlockedUser bu
        WHERE bu.chatBlocker.id = :blockerId AND bu.chatBlocked.id = :blockedId
    """)
    boolean existsByBlockerIdAndBlockedId(@Param("blockerId") Long receiverId, @Param("blockedId") Long senderId);

    @Query("SELECT bu FROM ChatBlockedUser bu WHERE bu.chatBlocker.id = :blockerId AND bu.chatBlocked.id = :blockedId")
    Optional<ChatBlockedUser> findByBlockerIdAndBlockedId(@Param("blockerId") Long blockerId, @Param("blockedId") Long blockedId);
}
