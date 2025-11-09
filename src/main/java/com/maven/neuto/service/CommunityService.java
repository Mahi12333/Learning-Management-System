package com.maven.neuto.service;

import com.maven.neuto.payload.request.community.CommunityUserDTO;
import com.maven.neuto.payload.request.community.CreateCommunityDTO;
import com.maven.neuto.payload.request.community.UpdatedCommunityDTO;
import com.maven.neuto.payload.response.PaginatedResponse;
import com.maven.neuto.payload.response.community.CommunityResponseDTO;
import com.maven.neuto.payload.response.community.SuperAdminCommunityPageResponseDTO;

public interface CommunityService {
    CommunityResponseDTO createCommunity(CreateCommunityDTO request);
    void deleteCommunity(Long communityId);
    CommunityResponseDTO updatedCommunity(UpdatedCommunityDTO request);
    PaginatedResponse<CommunityUserDTO> getUserCommunity(Integer pageNumber, Integer pageSize, String sortOrder);
    SuperAdminCommunityPageResponseDTO communityPageSuperAdmin(Long communityId, String userType, Integer pageNumber, Integer pageSize);
}
