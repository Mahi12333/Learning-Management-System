package com.maven.neuto.repository;

import com.maven.neuto.model.GroupComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupCommentRepository extends JpaRepository<GroupComment, Long> {
    Optional<GroupComment> findByParentId(Long parentId);
    Page<GroupComment> findByGroup_IdAndParent_Id(Long groupId, Long parentId, Pageable pageable);

    Page<GroupComment> findByParent_Id(Long parentIdOfReply, Pageable pageable);
}
