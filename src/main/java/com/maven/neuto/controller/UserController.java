package com.maven.neuto.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.maven.neuto.config.MessageConfig;
import com.maven.neuto.payload.request.user.AdminInviteDTO;
import com.maven.neuto.payload.request.user.InviteDTO;
import com.maven.neuto.payload.request.user.ProfileCompleteDTO;
import com.maven.neuto.payload.request.user.UpdatePasswordDTO;
import com.maven.neuto.payload.response.user.*;
import com.maven.neuto.service.UserService;
import com.maven.neuto.utils.AppConstants;
import com.maven.neuto.utils.AuthUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final AuthUtil authUtil;
    private final MessageConfig messageConfig;

    //! userName will be add into cookies... fot best practices...

    @PostMapping("/user-logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String refreshToken) {
        SecurityContextHolder.clearContext();
        userService.userLogout(refreshToken);
        return ResponseEntity.ok("Logged out successfully");
    }

    @PatchMapping("/password-change")
    public ResponseEntity<?> updatePassword(@Valid @RequestBody UpdatePasswordDTO request) {
        Long userId = authUtil.loggedInUserId();
        userService.updatePassword(userId, request);
        return ResponseEntity.ok("Password updated successfully!");
    }

    @PutMapping("/profile-complete")
    public ResponseEntity<?> profileComplete(@Valid @RequestBody ProfileCompleteDTO request){
        UserDetailsDTO response = userService.profileComplete(request);
         return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/userName-check")
    public ResponseEntity<?> userNameCheck(@Valid @RequestParam("userName") String userName, Locale locale){
        String response = userService.userNameCheck(userName);
        String localizedMessage = messageConfig.getMessage(response, null, locale);
         return new ResponseEntity<>(localizedMessage, HttpStatus.OK);
    }

    //! For Testing
    @GetMapping("/user-profile")
    public ResponseEntity<?> userProfile(){
        ProfileVisitDTO response = userService.userProfile();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/follow-unfollow-user")
    public ResponseEntity<?> followUnfollowUser(@Valid @RequestParam("userName") String userName){
        String response = userService.followUnfollowUser(userName);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/profile-visit")
    public ResponseEntity<?> profileVisit(@Valid  @RequestParam(value = "userName", required = false) String userName){
        ProfileVisitResponseDTO response = userService.profileVisit(userName);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/profile-visit-tabs")
    public ResponseEntity<?> profileVisitTabs(@Valid @RequestParam(value = "userName", required = true) String userName, @RequestParam(value = "tabs", required = true) String tabs,
                                              @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
                                              @RequestParam(name = "pageSize", defaultValue = AppConstants.LIMIT, required = false) Integer pageSize,
                                              @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder){
        ProfileVisitResponseDTO response = userService.profileVisitTabs(userName, tabs, pageNumber, pageSize, sortOrder);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //! CSV Not Upload
    @PostMapping(
            value = "/invite-member-teacher",
            consumes = { MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE }
    )
    public ResponseEntity<?>InvitedUser( @RequestPart(value = "data", required = false) String data,
                                          @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {

        InviteDTO request = new ObjectMapper().readValue(data, InviteDTO.class);
        InvitedResponse response = userService.invitedUser(request, file);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/invite-admin")
    public ResponseEntity<?>InvitedAdminUser(@Valid @RequestBody AdminInviteDTO request, Locale locale){
       String response = userService.invitedAdminUser(request);
       String localizedMessage = messageConfig.getMessage("admin.invite.send.success", null, locale);
       return ResponseEntity.ok(localizedMessage);
    }

    @GetMapping("/super-admin-profile")
    public ResponseEntity<?> SuperAdminProfile(@Valid @RequestParam("userName") String userName){
        SuperAdminProfileResponseDTO response = userService.superAdminProfile(userName);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/active-pending-super-admin")
    public ResponseEntity<?> PendingActiveSuperAdmin(@Valid @RequestParam("type") String type,
                                                     @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
                                                     @RequestParam(name = "pageSize", defaultValue = AppConstants.LIMIT, required = false) Integer pageSize,
                                                     @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder){
        //TODO
        ActivePendingUserDTO response = userService.PendingActiveSuperAdmin(type, pageNumber, pageSize, sortOrder);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/change-user-role")
    public ResponseEntity<?> changeUserRole(@Valid @RequestParam("userName") String userName, @RequestParam("role") Long role){
        userService.changeUserRole(userName, role);
        return ResponseEntity.ok("Role successfully change");
    }


}