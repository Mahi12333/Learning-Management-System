package com.maven.neuto.service;


import com.maven.neuto.payload.request.content.ShortContentCreateDTO;
import com.maven.neuto.payload.request.content.ShortContentUpdatedDTO;
import com.maven.neuto.payload.response.PaginatedResponse;
import com.maven.neuto.payload.response.content.ShortContentResponseDTO;
import com.maven.neuto.payload.response.content.UserShortContentResponseDTO;
import jakarta.validation.Valid;

public interface ContentService {
    ShortContentResponseDTO createShortContent(ShortContentCreateDTO request);
    ShortContentResponseDTO updatedShortContent(ShortContentUpdatedDTO request);
    String deleteShortContent(Long shortContentId);
    String likeDislikeShortContent(Long shortContentId);
    PaginatedResponse<UserShortContentResponseDTO> fetchShortContent(Long shortContentId, Integer pageNumber, Integer pageSize, String sortOrder);
    String bookMarkShortContent(Long shortContentId);
}
