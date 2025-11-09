package com.maven.neuto.utils;

import com.maven.neuto.emun.BookMark;
import com.maven.neuto.exception.APIException;
import com.maven.neuto.model.Bookmark;
import com.maven.neuto.model.ShortContentUpload;
import com.maven.neuto.model.User;
import com.maven.neuto.repository.BookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShortContentHelperMethod {
     private final BookmarkRepository bookmarkRepository;

    public void deleteShortContentBookMarkByUserAndStatusAndShortContentUploadId(User user, BookMark bookMark, ShortContentUpload content) {
        bookmarkRepository.deleteShortContentBookMarkByUserAndStatusAndShortContentUploadId(user, bookMark, content);

    }

    public void saveShortContentBookMarkByUserAndStatusAndShortContentUploadId(User user, BookMark bookMark, ShortContentUpload content) {
        boolean alreadyBookmarked = bookmarkRepository
                .existsShortContentBookMarkByUserAndStatusAndShortContentUploadId(user, BookMark.LIKE, content);
        if (alreadyBookmarked) {
            throw new APIException("shortcontent.already.bookmarked", HttpStatus.BAD_REQUEST);
        }
        Bookmark bookmark = Bookmark.builder()
                .type(bookMark)
                .bookmarkUser(user)
                .bookmarkShortContent(content)
                .build();
        bookmarkRepository.save(bookmark);
    }

    public void saveShortContentBookMarkByUserAndTypeAndShortContentUploadId(User user, BookMark bookMark, ShortContentUpload content) {
        boolean alreadyBookmarked = bookmarkRepository
                .existsShortContentBookMarkByUserAndStatusAndShortContentUploadId(user, BookMark.BOOKMARK, content);
        if (alreadyBookmarked) {
            throw new APIException("shortcontent.already.bookmarked", HttpStatus.BAD_REQUEST);
        }
        Bookmark bookmark = Bookmark.builder()
                .type(bookMark)
                .bookmarkUser(user)
                .bookmarkShortContent(content)
                .build();
        bookmarkRepository.save(bookmark);
    }
}
