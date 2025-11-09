package com.maven.neuto.repository;

import com.maven.neuto.model.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("Select m from ChatMessage m Where (m.sender.id = :userId OR m.receiver.id = :userId) And m.createdAt = (Select Max(m2.createdAt) from ChatMessage m2 Where (m2.sender.id = :userId OR m2.receiver.id = :userId) And ((m2.sender.id = m.sender.id AND m2.receiver.id = m.receiver.id) OR (m2.sender.id = m.receiver.id AND m2.receiver.id = m.sender.id)) ORDER BY m.createdAt DESC)")
    Page<ChatMessage> findLatestChats(@Param("userId") Long currentUserId, Pageable pageable);

    @Query("Select count(m) from ChatMessage m Where m.receiver.id = :userId AND m.sender.id = :senderId AND m.isSeen = false")
    Long countUnreadMessages(@Param("userId") Long currentUserId, @Param("senderId") Long id);

    @Query("""
    SELECT m FROM ChatMessage m
    WHERE (m.sender.id = :userId OR m.receiver.id = :userId)
      AND (:deletedIds IS NULL OR m.id NOT IN :deletedIds)
      AND m.id IN (
          SELECT MAX(m2.id)
          FROM ChatMessage m2
          WHERE (m2.sender.id = :userId OR m2.receiver.id = :userId)
          GROUP BY CASE WHEN m2.sender.id = :userId THEN m2.receiver.id ELSE m2.sender.id END
      )
    ORDER BY m.createdAt DESC
""")
    List<ChatMessage> findLatestMessagesForUsers(@Param("userId") Long userId,
                                                 @Param("deletedIds") List<Long> deletedIds);

//    @Query("SELECT m.sender.id, COUNT(m) FROM ChatMessage m WHERE m.receiver.id = :userId AND m.isSeen = false GROUP BY m.sender.id")
//    Map<Long, Long> countUnreadMessagesForUser(@Param("userId") Long currentUserId);
    @Query("SELECT m.sender.id, COUNT(m) FROM ChatMessage m WHERE m.receiver.id = :userId AND m.isSeen = false GROUP BY m.sender.id")
        List<Object[]> countUnreadMessagesForUser(@Param("userId") Long currentUserId);

    @Transactional
    @Modifying
    @Query("""
        UPDATE ChatMessage m SET m.isSeen = true
        WHERE m.conversation.id = :conversationId
        AND m.sender.id = :senderId
        AND m.receiver.id = :receiverId
    """)
    void markMessagesAsSeen(@Param("conversationId") Long id, @Param("senderId") Long senderId, @Param("receiverId") Long receiverId);


    @Query("""
        SELECT m FROM ChatMessage m
        WHERE m.conversation.id = :conversationId
        AND m.id NOT IN :deletedIds
    """)
    Page<ChatMessage> findVisibleMessages(@Param("conversationId") Long conversationId,
                                          @Param("deletedIds") List<Long> deletedIds,
                                          Pageable pageable);

    @Query("""
        SELECT COUNT(m) FROM ChatMessage m
        WHERE m.conversation.id = :conversationId
        AND m.id NOT IN :deletedIds
    """)
    Long countVisibleMessages(@Param("conversationId") Long conversationId,
                              @Param("deletedIds") List<Long> deletedIds);

    @Query("SELECT m FROM ChatMessage m WHERE m.conversation.id = :id ORDER BY m.createdAt ASC")
    List<ChatMessage> findByConversationId(@Param("id") Long id);

    @Query("""
    SELECT COUNT(m)
    FROM ChatMessage m
    WHERE m.receiver.id = :receiverId
      AND m.sender.id = :senderId
      AND m.isSeen = false
""")
    Long countUnreadMessagesBetweenUsers(@Param("receiverId") Long receiverId,
                                         @Param("senderId") Long senderId);

}
