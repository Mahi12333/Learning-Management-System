package com.maven.neuto.service;


import com.maven.neuto.payload.request.banner.BannerCreateDTO;
import com.maven.neuto.payload.request.banner.BannerUpdateDTO;
import com.maven.neuto.payload.response.PaginatedResponse;
import com.maven.neuto.payload.response.banner.BannerResponseDTO;
import jakarta.validation.Valid;

public interface BannerService {
    void deleteBanner(Long bannerId);
    BannerResponseDTO createBanner(BannerCreateDTO request);
    BannerResponseDTO updatedBanner(BannerUpdateDTO request);
    PaginatedResponse<BannerResponseDTO> getBanner(Integer pageNumber, Integer pageSize, String sortOrder);
}
