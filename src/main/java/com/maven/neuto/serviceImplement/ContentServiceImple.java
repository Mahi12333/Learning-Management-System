package com.maven.neuto.serviceImplement;

import com.maven.neuto.emun.BookMark;
import com.maven.neuto.exception.ResourceNotFoundException;
import com.maven.neuto.mapstruct.ShortContentMapper;
import com.maven.neuto.model.ShortContentUpload;
import com.maven.neuto.model.User;
import com.maven.neuto.payload.request.content.ShortContentCreateDTO;
import com.maven.neuto.payload.request.content.ShortContentUpdatedDTO;
import com.maven.neuto.payload.response.PaginatedResponse;
import com.maven.neuto.payload.response.banner.BannerResponseDTO;
import com.maven.neuto.payload.response.content.ShortContentResponseDTO;
import com.maven.neuto.payload.response.content.UserShortContentResponseDTO;
import com.maven.neuto.repository.BookmarkRepository;
import com.maven.neuto.repository.ShortContentRepository;
import com.maven.neuto.service.ContentService;
import com.maven.neuto.utils.AuthUtil;
import com.maven.neuto.utils.ShortContentHelperMethod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContentServiceImple implements ContentService {
    private final AuthUtil authUtil;
    private final ShortContentMapper shortContentMapper;
    private final ShortContentRepository shortContentRepository;
    private final BookmarkRepository bookmarkRepository;
    private final ShortContentHelperMethod shortContentHelperMethod;

    @Transactional
    @Override
    public ShortContentResponseDTO createShortContent(ShortContentCreateDTO request) {
        User currentUser = authUtil.currentUser();
        ShortContentUpload content = shortContentMapper.toCreateShortContentEntity(request);
        content.setCreator(currentUser);
        content.setShortContentCommunity(currentUser.getUserCommunity());
        ShortContentUpload saved = shortContentRepository.save(content);
        return shortContentMapper.toShortContentResponseDto(saved);
    }

    @Transactional
    @Override
    public ShortContentResponseDTO updatedShortContent(ShortContentUpdatedDTO request) {
        ShortContentUpload content = shortContentRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("shortcontent.notfound"));
        if(request.isOnlyFetch() == true){
            return shortContentMapper.toShortContentResponseDto(content);
        }
        shortContentMapper.updateShortContentFromDto(request, content);
        ShortContentUpload updated = shortContentRepository.save(content);
        return shortContentMapper.toShortContentResponseDto(updated);
    }

    @Transactional
    @Override
    public String deleteShortContent(Long shortContentId) {
        ShortContentUpload content = shortContentRepository.findById(shortContentId)
                .orElseThrow(() -> new ResourceNotFoundException("shortcontent.notfound"));
        shortContentRepository.delete(content);
        return "shortcontent.deleted.successfully";
    }

    @Transactional
    @Override
    public String likeDislikeShortContent(Long shortContentId) {
        ShortContentUpload content = shortContentRepository.findById(shortContentId)
                .orElseThrow(() -> new ResourceNotFoundException("shortcontent.notfound"));
        boolean existingBookmark = bookmarkRepository.existsShortContentBookMarkByUserAndStatusAndShortContentUploadId(
                authUtil.currentUser(), BookMark.LIKE, content);
        if(existingBookmark) {
            shortContentHelperMethod.deleteShortContentBookMarkByUserAndStatusAndShortContentUploadId(
                    authUtil.currentUser(), BookMark.LIKE, content);
            return "shortcontent.disliked.successfully";
        }else{
            shortContentHelperMethod.saveShortContentBookMarkByUserAndStatusAndShortContentUploadId(
                    authUtil.currentUser(), BookMark.LIKE, content);
            return "shortcontent.liked.successfully";
        }
    }

    @Override
    public PaginatedResponse<UserShortContentResponseDTO> fetchShortContent(Long shortContentId, Integer pageNumber, Integer pageSize, String sortOrder) {
        ShortContentUpload content = shortContentRepository.findById(shortContentId)
                .orElseThrow(() -> new ResourceNotFoundException("shortcontent.notfound"));

        Long currentUserId = authUtil.loggedInUserIdForTesting(); // or your method for getting logged-in user
        Sort sort = sortOrder.equalsIgnoreCase("desc") ?
                Sort.by("createdAt").descending() :
                Sort.by("createdAt").ascending();


        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        // Fetch paginated short content (all or filtered)
        Page<ShortContentUpload> shortContentPage = shortContentRepository.findAll(pageable);

        // Fetch all content IDs on this page
        List<Long> contentIds = shortContentPage.stream()
                .map(ShortContentUpload::getId)
                .toList();

        // Fetch bookmarks & likes for this user for all contentIds (to avoid N+1)
        List<Long> bookmarkedIds = bookmarkRepository.findBookmarkedContentIdsByUserIdAndType(currentUserId, contentIds, BookMark.BOOKMARK);
        List<Long> likedIds = bookmarkRepository.findLikedContentIdsByUserIdAndType(currentUserId, contentIds, BookMark.LIKE);

        // Map entity -> DTO
        List<UserShortContentResponseDTO> contentDTOs = shortContentPage.getContent().stream()
                .map(contents -> UserShortContentResponseDTO.builder()
                        .id(content.getId())
                        .imagesPath(content.getImagesPath())
                        .thumbnail(content.getThumbnail())
                        .videoDuration(content.getVideoDuration())
                        .size(content.getSize())
                        .createdAt(LocalDateTime.ofInstant(content.getCreatedAt(), ZoneOffset.UTC))
                        .updatedAt(LocalDateTime.ofInstant(content.getUpdatedAt(), ZoneOffset.UTC))
                        .caption(content.getCaption())
                        .isBookmark(bookmarkedIds.contains(content.getId()))
                        .isLike(likedIds.contains(content.getId()))
                        .build())
                .toList();

        return PaginatedResponse.<UserShortContentResponseDTO>builder()
                .content(contentDTOs)
                .pageNumber(shortContentPage.getNumber())
                .pageSize(shortContentPage.getSize())
                .totalElements(shortContentPage.getTotalElements())
                .totalPages(shortContentPage.getTotalPages())
                .build();


    }

    @Transactional
    @Override
    public String bookMarkShortContent(Long shortContentId) {
        ShortContentUpload content = shortContentRepository.findById(shortContentId)
                .orElseThrow(() -> new ResourceNotFoundException("shortcontent.notfound"));
        boolean existingBookmark = bookmarkRepository.existsShortContentBookMarkByUserAndStatusAndShortContentUploadId(
                authUtil.currentUser(), BookMark.BOOKMARK, content);
        if(existingBookmark) {
            shortContentHelperMethod.deleteShortContentBookMarkByUserAndStatusAndShortContentUploadId(
                    authUtil.currentUser(), BookMark.BOOKMARK, content);
            return "shortcontent.bookmark.successfully";
        }else{
            shortContentHelperMethod.saveShortContentBookMarkByUserAndTypeAndShortContentUploadId(
                    authUtil.currentUser(), BookMark.BOOKMARK, content);
            return "shortcontent.bookmark.successfully";
        }
    }
}
