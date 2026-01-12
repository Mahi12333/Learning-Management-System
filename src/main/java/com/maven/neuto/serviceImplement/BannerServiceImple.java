package com.maven.neuto.serviceImplement;

import com.maven.neuto.aspect.Auditable;
import com.maven.neuto.exception.APIException;
import com.maven.neuto.exception.ResourceNotFoundException;
import com.maven.neuto.mapstruct.BannerMapper;
import com.maven.neuto.mapstruct.MapperContext;
import com.maven.neuto.model.Banner;
import com.maven.neuto.payload.request.banner.BannerCreateDTO;
import com.maven.neuto.payload.request.banner.BannerUpdateDTO;
import com.maven.neuto.payload.response.PaginatedResponse;
import com.maven.neuto.payload.response.banner.BannerResponseDTO;
import com.maven.neuto.repository.BannerRepository;
import com.maven.neuto.repository.CommunityRepository;
import com.maven.neuto.service.BannerService;
import com.maven.neuto.utils.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BannerServiceImple implements BannerService {
    private final AuthUtil authUtil;
    private final BannerRepository bannerRepository;
    private final BannerMapper bannerMapper;
    private final CommunityRepository communityRepository;
    private final MapperContext mapperContext;

    @Auditable(action = "DELETE", entity = "BANNER")
    @Override
    public void deleteBanner(Long bannerId) {
        Banner banner = bannerRepository.findById(bannerId).orElseThrow(()-> new ResourceNotFoundException("Banner not found!"));
        bannerRepository.delete(banner);
    }

    @Auditable(action = "CREATE", entity = "BANNER")
    @Override
    public BannerResponseDTO createBanner(BannerCreateDTO request) {
        Long communityId = authUtil.communityId();
        Long userId = authUtil.loggedInUserIdForTesting();
        if(bannerRepository.existsByNameAndCommunityId(request.getName(), communityId)){
            throw new APIException("Banner name already exists", HttpStatus.BAD_REQUEST);
        }
        Banner banner = bannerMapper.toEntity(request, communityId, userId, mapperContext );
        Banner savedBanner = bannerRepository.save(banner);
        return bannerMapper.toResponseDto(savedBanner);
    }

    @Auditable(action = "UPDATE", entity = "BANNER")
    @Override
    public BannerResponseDTO updatedBanner(BannerUpdateDTO request) {
        Banner banner = bannerRepository.findById(request.getBannerId()).orElseThrow(()-> new ResourceNotFoundException("Banner not found!"));
        bannerMapper.updateBannerFromDto(request, banner);
        Banner updatedBanner = bannerRepository.save(banner);
        return bannerMapper.toResponseDto(updatedBanner);
    }

    @Override
    public PaginatedResponse<BannerResponseDTO> getBanner(Integer pageNumber, Integer pageSize, String sortOrder) {
        Long communityId = authUtil.communityId();
        Sort sort = sortOrder.equalsIgnoreCase("desc") ? Sort.by("createdAt").descending() : Sort.by("createdAt").ascending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Banner> bannerPage = bannerRepository.findAllByCommunityId(communityId, pageable);
        List<BannerResponseDTO> bannerResponseDTOS = bannerPage.stream().map(bannerMapper::toResponseDto).toList();

        return PaginatedResponse.<BannerResponseDTO>builder()
                .content(bannerResponseDTOS)
                .pageNumber(bannerPage.getNumber())
                .pageSize(bannerPage.getSize())
                .totalElements(bannerPage.getTotalElements())
                .totalPages(bannerPage.getTotalPages())
                .build();
    }

}
