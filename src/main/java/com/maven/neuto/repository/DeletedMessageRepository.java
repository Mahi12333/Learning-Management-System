package com.maven.neuto.repository;

import com.maven.neuto.model.DeletedMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.BitSet;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeletedMessageRepository extends JpaRepository<DeletedMessage, Long> {
    @Query("SELECT dm.message.id FROM DeletedMessage dm WHERE dm.user.id = :userId")
    List<Long> findMessageIdsByUserId(@Param("userId") Long currentUserId);

    Optional<DeletedMessage> findByUserIdAndMessageId(Long currentUserId, Long id);
}
