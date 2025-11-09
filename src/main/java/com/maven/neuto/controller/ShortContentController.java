package com.maven.neuto.controller;


import com.maven.neuto.config.MessageConfig;
import com.maven.neuto.payload.request.content.ShortContentCreateDTO;
import com.maven.neuto.payload.request.content.ShortContentUpdatedDTO;
import com.maven.neuto.payload.response.PaginatedResponse;
import com.maven.neuto.payload.response.content.ShortContentResponseDTO;
import com.maven.neuto.payload.response.content.UserShortContentResponseDTO;
import com.maven.neuto.service.ContentService;
import com.maven.neuto.utils.AppConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@Slf4j
@RestController
@RequestMapping("/api/v1/short-content")
@RequiredArgsConstructor
public class ShortContentController {
    private final MessageConfig messageConfig;
    private final ContentService contentService;


    @PostMapping("/create-short-content")
    public ResponseEntity<?> createShortContent(@Valid @RequestBody ShortContentCreateDTO request){
        log.info("create content: {}", request);
        ShortContentResponseDTO response = contentService.createShortContent(request);
         return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/updated-short-content")
    public ResponseEntity<?> updatedShortContent(@RequestBody ShortContentUpdatedDTO request){
        ShortContentResponseDTO response = contentService.updatedShortContent(request);
        //String localizedMessage = messageConfig.getMessage(response, null, locale);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/delete-short-content")
    public ResponseEntity<?> deleteShortContent(@Valid @RequestParam("shortContentId") Long shortContentId, Locale locale){
        String response = contentService.deleteShortContent(shortContentId);
        String localizedMessage = messageConfig.getMessage(response, null, locale);
        return new ResponseEntity<>(localizedMessage, HttpStatus.CREATED);
    }

    @PostMapping("/like-dislike-short-content")
    public ResponseEntity<?> likeDislikeShortContent(@RequestParam("shortContentId") Long shortContentId, Locale locale){
        String response = contentService.likeDislikeShortContent(shortContentId);
        String localizedMessage = messageConfig.getMessage(response, null, locale);
        return new ResponseEntity<>(localizedMessage, HttpStatus.CREATED);
    }

    @GetMapping("/fetch-short-content")
    public ResponseEntity<?> fetchShortContent(@RequestParam("shortContentId") Long shortContentId,
                                               @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
                                               @RequestParam(name = "pageSize", defaultValue = AppConstants.LIMIT, required = false) Integer pageSize,
                                               @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder){
        PaginatedResponse<UserShortContentResponseDTO> response = contentService.fetchShortContent(shortContentId, pageNumber, pageSize, sortOrder);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/bookmark-short-content")
    public ResponseEntity<?> bookMarkShortContent(@Valid @RequestParam("shortContentId") Long shortContentId, Locale locale){
        String response = contentService.bookMarkShortContent(shortContentId);
        String localizedMessage = messageConfig.getMessage(response, null, locale);
        return new ResponseEntity<>(localizedMessage, HttpStatus.CREATED);
    }


}
