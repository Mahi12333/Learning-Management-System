package com.maven.neuto.repository;

import com.maven.neuto.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    @Query("""
        SELECT c FROM Conversation c
        JOIN c.participants p1
        JOIN c.participants p2
        WHERE p1.conversationParticipantUser.id = :senderId AND p2.conversationParticipantUser.id = :receiverId
    """)
    Optional<Conversation> findByParticipants(@Param("senderId") Long senderId, @Param("receiverId") Long receiverId);
}
