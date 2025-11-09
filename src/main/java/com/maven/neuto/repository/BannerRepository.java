package com.maven.neuto.repository;

import com.maven.neuto.model.Banner;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Banner b WHERE b.name = :name AND b.communityId = :communityId")
    boolean existsByNameAndCommunityId(@Param("name") String name, @Param("communityId") Long communityId);

    Page<Banner> findAllByCommunityId(Long communityId, Pageable pageable);

    Long countByCommunityId(Long communityId);

    @Query("SELECT SUM(b.size) FROM Banner b WHERE b.communityId = :communityId")
    Long sumSizeByCommunityId(@Param("communityId") Long communityId);
}
