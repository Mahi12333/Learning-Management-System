package com.maven.neuto.controller;


import com.maven.neuto.mapstruct.CommunityMapper;
import com.maven.neuto.payload.request.community.CommunityUserDTO;
import com.maven.neuto.payload.request.community.CreateCommunityDTO;
import com.maven.neuto.payload.request.community.UpdatedCommunityDTO;
import com.maven.neuto.payload.response.PaginatedResponse;
import com.maven.neuto.payload.response.community.CommunityResponseDTO;
import com.maven.neuto.payload.response.community.SuperAdminCommunityPageResponseDTO;
import com.maven.neuto.service.CommunityService;
import com.maven.neuto.utils.AppConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/community")
@RequiredArgsConstructor
public class CommunityController {
    private final CommunityService communityService;

    @PostMapping("/create-community")
    public ResponseEntity<?> createCommunity(@Valid @RequestBody CreateCommunityDTO request){
        CommunityResponseDTO response = communityService.createCommunity(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/delete-community")
    public ResponseEntity<?> deleteCommunity(@Valid @RequestParam("communityId") Long communityId){
            communityService.deleteCommunity(communityId);
            return ResponseEntity.ok("Community deleted successfully");
    }

    @PatchMapping("/updated-community")
    public ResponseEntity<?> updatedCommunity(@RequestBody UpdatedCommunityDTO request){
        CommunityResponseDTO response = communityService.updatedCommunity(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/user-community")
    public ResponseEntity<PaginatedResponse<CommunityUserDTO>> getUserCommunity(@RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
                                                                                @RequestParam(name = "pageSize", defaultValue = AppConstants.LIMIT, required = false) Integer pageSize,
                                                                                @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder){
    PaginatedResponse<CommunityUserDTO> response = communityService.getUserCommunity(pageNumber, pageSize, sortOrder);
    return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @GetMapping("/community-page-super-admin")
    public ResponseEntity<?> communityPageSuperAdmin(@Valid @RequestParam("communityId") Long communityId, @RequestParam("userType") String userType,  @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
                                                     @RequestParam(name = "pageSize", defaultValue = AppConstants.LIMIT, required = false) Integer pageSize){
        SuperAdminCommunityPageResponseDTO response = communityService.communityPageSuperAdmin(communityId, userType, pageNumber, pageSize);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
