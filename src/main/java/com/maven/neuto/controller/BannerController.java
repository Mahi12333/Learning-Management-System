package com.maven.neuto.controller;


import com.maven.neuto.payload.request.banner.BannerCreateDTO;
import com.maven.neuto.payload.request.banner.BannerUpdateDTO;
import com.maven.neuto.payload.response.PaginatedResponse;
import com.maven.neuto.payload.response.banner.BannerResponseDTO;
import com.maven.neuto.service.BannerService;
import com.maven.neuto.utils.AppConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/api/v1/banner")
@RequiredArgsConstructor
public class BannerController {
    private final BannerService bannerService;

    @PostMapping("/create-banner")
    public ResponseEntity<?> createBanner(@Valid @RequestBody BannerCreateDTO request){
        BannerResponseDTO response = bannerService.createBanner(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("/update-banner")
    public ResponseEntity<?> updateBanner(@Valid @RequestBody BannerUpdateDTO request){
        BannerResponseDTO response = bannerService.updatedBanner(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/delete-banner")
    public ResponseEntity<?> deleteBanner(@Valid @RequestParam("bannerId") Long bannerId){
        bannerService.deleteBanner(bannerId);
        return ResponseEntity.ok("Banner delete successfully");
    }

    @GetMapping("/banner")
    public ResponseEntity<PaginatedResponse<BannerResponseDTO>> banner(@RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
                                                                       @RequestParam(name = "pageSize", defaultValue = AppConstants.LIMIT, required = false) Integer pageSize,
                                                                       @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder){

        PaginatedResponse<BannerResponseDTO> response = bannerService.getBanner(pageNumber,pageSize,sortOrder);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }






}
