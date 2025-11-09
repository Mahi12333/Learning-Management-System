package com.maven.neuto.repository;

import com.maven.neuto.model.ShortContentUpload;
import com.maven.neuto.payload.response.content.UserShortContentResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShortContentRepository extends JpaRepository<ShortContentUpload, Long> {
    @Query("SELECT COUNT(s) FROM ShortContentUpload s WHERE s.shortContentCommunity.id = :communityId")
    Long countByCommunityId(Long communityId);
    @Query("SELECT SUM(s.size) FROM ShortContentUpload s WHERE s.shortContentCommunity.id = :communityId")
    Long sumSizeByCommunityId(@Param("communityId") Long communityId);

    @Query("SELECT sc FROM ShortContentUpload sc WHERE sc.creator.id = :userId OR sc.id IN (SELECT b.bookmarkShortContent.id FROM Bookmark b WHERE b.bookmarkUser.id = :userId)")
    Page<ShortContentUpload> findVisibleContentsForUser(@Param("userId") Long userId, Pageable pageable);

}
