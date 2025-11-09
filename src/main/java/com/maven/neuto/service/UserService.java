package com.maven.neuto.service;


import com.maven.neuto.model.User;
import com.maven.neuto.payload.request.user.AdminInviteDTO;
import com.maven.neuto.payload.request.user.InviteDTO;
import com.maven.neuto.payload.request.user.ProfileCompleteDTO;
import com.maven.neuto.payload.request.user.UpdatePasswordDTO;
import com.maven.neuto.payload.response.user.*;
import jakarta.validation.Valid;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface UserService {
    void userLogout(String refreshToken);
    void updatePassword(Long userId, UpdatePasswordDTO request);
    String userNameCheck(String userName);
    String invitedAdminUser(AdminInviteDTO request);
    InvitedResponse invitedUser(InviteDTO request, MultipartFile file);
    UserDetailsDTO profileComplete(ProfileCompleteDTO request);
    String followUnfollowUser(String userName);
    ProfileVisitResponseDTO profileVisit(String userName);
    SuperAdminProfileResponseDTO superAdminProfile(String userName);
    void changeUserRole(String userName, Long role);
    ActivePendingUserDTO PendingActiveSuperAdmin(String type, Integer pageNumber, Integer pageSize, String sortOrder);

    Optional<User> findByEmail(String email);

    void registerUser(User newUser);
    ProfileVisitDTO userProfile();
    ProfileVisitResponseDTO profileVisitTabs(String userName, String tabs, Integer pageNumber, Integer pageSize, String sortOrder);
}
