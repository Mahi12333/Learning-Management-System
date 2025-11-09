package com.maven.neuto.repository;

import com.maven.neuto.emun.BookMark;
import com.maven.neuto.model.Bookmark;
import com.maven.neuto.model.ShortContentUpload;
import com.maven.neuto.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Bookmark b WHERE b.bookmarkUser = :user AND b.type = :bookMark AND b.bookmarkShortContent = :content")
    boolean existsShortContentBookMarkByUserAndStatusAndShortContentUploadId(@Param("user") User user, @Param("bookMark") BookMark bookMark, @Param("content") ShortContentUpload content);

    @Query("DELETE FROM Bookmark b WHERE b.bookmarkUser = :user AND b.type = :bookMark AND b.bookmarkShortContent = :content")
    void deleteShortContentBookMarkByUserAndStatusAndShortContentUploadId(@Param("user") User user, @Param("bookMark") BookMark bookMark, @Param("content") ShortContentUpload content);


    @Query("Select b.bookmarkShortContent.id FROM Bookmark b Where b.bookmarkUser.id = :currentUserId AND b.type = :bookMark AND b.bookmarkShortContent.id IN :contentIds")
    List<Long> findBookmarkedContentIdsByUserIdAndType(@Param("currentUserId") Long currentUserId, @Param("contentIds") List<Long> contentIds, @Param("bookMark") BookMark bookMark);

    @Query("Select b.bookmarkShortContent.id FROM Bookmark b Where b.bookmarkUser.id = :currentUserId AND b.type = :bookMark AND b.bookmarkShortContent.id IN :contentIds")
    List<Long> findLikedContentIdsByUserIdAndType(@Param("currentUserId") Long currentUserId, @Param("contentIds") List<Long> contentIds, @Param("bookMark") BookMark bookMark);
}
