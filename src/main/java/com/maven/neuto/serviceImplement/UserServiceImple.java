package com.maven.neuto.serviceImplement;


import com.maven.neuto.emun.*;
import com.maven.neuto.exception.APIException;
import com.maven.neuto.exception.ResourceNotFoundException;
import com.maven.neuto.mapstruct.UserMapper;
import com.maven.neuto.model.NotificationPermission;
import com.maven.neuto.model.Role;
import com.maven.neuto.model.User;
import com.maven.neuto.model.UserFollow;
import com.maven.neuto.payload.request.user.*;
import com.maven.neuto.payload.response.content.UserShortContentResponseDTO;
import com.maven.neuto.payload.response.course.UserCourseResponseDTO;
import com.maven.neuto.payload.response.group.UserGroupResponseDTO;
import com.maven.neuto.payload.response.user.*;
import com.maven.neuto.repository.*;
import com.maven.neuto.service.UserService;
import com.maven.neuto.utils.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImple implements UserService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final AuthUtil authUtil;
    private final EmailService emailService;
    private final NotificationPermissionRepository notificationPermissionRepository;
    private final UserFollowRepository userFollowRepository;
    private final CommunityRepository communityRepository;
    private final RoleRepository roleRepository;
    private final HelperMethod helperMethod;
    private final CourseRepository courseRepository;
    private final ShortContentRepository shortContentRepository;
    private final GroupUserRepository groupUserRepository;
    private final UserHelperMethod userHelperMethod;
    private final TemplateHelper templateHelper;
    private final NotificationService notificationService;

    @Value("${PROFILE_URL}")
    private String PROFILE_URL;

    @Value("${BACKGROUND_COVER_URL}")
    private String BACKGROUND_COVER_URL;


    @Transactional
    @Override
    public void userLogout(String refreshToken) {
        refreshTokenRepository.deleteByRefreshToken(refreshToken);
    }

    @Override
    public void updatePassword(Long userId, UpdatePasswordDTO request) {
        User user = userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("User not found!"));
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new APIException("Old password is incorrect", HttpStatus.BAD_REQUEST);
        }
        if (request.getNewPassword().equals(request.getOldPassword())) {
            throw new APIException("New password cannot be same as old password", HttpStatus.BAD_REQUEST);
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public String userNameCheck(String userName) {
        boolean exists = userRepository.existsByUserName(userName);
        if (exists) {
            return "username.already.exists";
        } else {
            return "username.available";
        }
    }

    @Transactional
    @Override
    public String invitedAdminUser(AdminInviteDTO request) {
        String email = request.getEmail().trim().toLowerCase();
        if (userRepository.findByEmailIgnoreCase(email).isPresent()) {
            throw new APIException("user.already.exists", HttpStatus.BAD_REQUEST);
        }
        String baseUserName = request.getEmail().split("@")[0].toLowerCase();
        String uniqueUserName = baseUserName;
        int count = 1;

        // 3. Loop to ensure uniqueness
        while (userRepository.existsByUserName(uniqueUserName)) {
            uniqueUserName = baseUserName + count;
            count++;
        }
        User user = userMapper.toEntityAdminInvited(request);
        user.setUserName(uniqueUserName);
        user.setProfilePicture(PROFILE_URL);
        user.setCoverPhoto(BACKGROUND_COVER_URL);
        userRepository.save(user);

        String subject = "You are Invited as a Admin";
        String encryptedEmail = helperMethod.encryptEmail(user.getEmail().trim());
        String unsubscribeLink = "https://yourdomain.com/unsubscribe?email=" + encryptedEmail;
        String InviteLink = "https://yourdomain.com/signup?email=" + encryptedEmail;


        Map<String, Object> model = Map.of(
                "name", user.getFirstName(),
                "InviteLink", InviteLink,
                "unsubscribeLink", unsubscribeLink,
                "subject", subject,
                "year", Year.now().getValue(),
                "userAccess", true
        );

        try {
            String htmlContent = templateHelper.buildEmail("Invite-user", model);
            emailService.sendEmail(user.getEmail(), subject, htmlContent);
        } catch (Exception e) {
            log.error("Failed to password reset email: {}", e);
            throw new APIException("send.email.failed", HttpStatus.BAD_REQUEST);
        }

        return "admin.invite.send.success";
    }

    @Transactional
    @Override
    public InvitedResponse invitedUser(InviteDTO request, MultipartFile file) {
        log.info("request--- {} and {}", request.getInviteType(), request.getUserType() );
        log.info("file--- {}", file);
        Long CurrentUserId = authUtil.loggedInUserIdForTesting();
        User CurrentUser = userRepository.findById(CurrentUserId).orElseThrow(()-> new ResourceNotFoundException("User not found!"));
        Long communityId = CurrentUser.getUserCommunity().getId();
        List<String> emails = new ArrayList<>();

        if (request.getInviteType().equalsIgnoreCase(UserInviteType.INDIVIDUAL.toString())) {
            emails = List.of(request.getEmail().toLowerCase().trim());
        } else if (request.getInviteType().equalsIgnoreCase(UserInviteType.MULTIPLE.toString())) {
            emails = HelperMethod.extractEmailsFromCSV(file);
            log.info("email: {}", emails);
        } else {
            throw new APIException("Invalid inviteType: " + request.getInviteType(), HttpStatus.BAD_REQUEST);
        }

        List<InviteResultDTO> results = new ArrayList<>();
        int invited = 0, skipped = 0, failed = 0;

        for (String email : emails) {
            try {
                if (!HelperMethod.isValid(email)) {
                    failed++;
                    results.add(new InviteResultDTO(email, null, "failed", "Invalid email format"));
                    continue;
                }

                if (userRepository.findByEmailIgnoreCase(email).isPresent()) {
                    skipped++;
                    results.add(new InviteResultDTO(email, null, "skipped", "User already exists"));
                    continue;
                }

                Long roleToAssign = request.getUserType().equalsIgnoreCase(UserType.CONTRIBUTOR.toString()) ? 3L : 4L;
                Role role = new Role();
                role.setId(roleToAssign);

                String baseUserName = request.getEmail().split("@")[0].toLowerCase();
                String uniqueUserName = baseUserName;
                int count = 1;

                // 3. Loop to ensure uniqueness
                while (userRepository.existsByUserName(uniqueUserName)) {
                    uniqueUserName = baseUserName + count;
                    count++;
                }

                User newUser = new User();
                newUser.setEmail(email);
                newUser.setRole(role);
                newUser.setInvitedBy(CurrentUser);
                newUser.setUserCommunity(CurrentUser.getUserCommunity());
                newUser.setCommunityComplete(ProfileComplete.COMPLETE);
                newUser.setProfilePicture(PROFILE_URL);
                newUser.setCoverPhoto(BACKGROUND_COVER_URL);
                newUser.setUserName(uniqueUserName);

                User saveUser = userRepository.save(newUser);

                String subject = roleToAssign == 3L ?
                        "Hi! You are invited as a Contributor/Teacher!" :
                        "Hi! You are invited as a Member!";
                String encryptedEmail = helperMethod.encryptEmail(email);
                String unsubscribeLink = "https://yourdomain.com/unsubscribe?email=" + encryptedEmail;
                String InviteLink = "https://yourdomain.com/signup?email=" + encryptedEmail;

                Map<String, Object> model = Map.of(
                        "name", Optional.ofNullable(saveUser.getFirstName()).orElse("User"),
                        "weburl", "https://yourdomain.com",
                        "signup", InviteLink,
                        "unsubscribeLink", unsubscribeLink,
                        "subject", subject,
                        "year", Year.now().getValue(),
                        "userAccess", true
                );

                try {
                    String htmlContent = templateHelper.buildEmail("Invite-user", model);
                    emailService.sendEmail(saveUser.getEmail(), subject, htmlContent);
                } catch (Exception e) {
                    log.error("Failed to Invite-user email: {}", e);
                    throw new APIException("send.email.failed", HttpStatus.BAD_REQUEST);
                }

                invited++;
                results.add(new InviteResultDTO(email, "invited", null, null));

            } catch (Exception e) {
                failed++;
                results.add(new InviteResultDTO(email, null, "failed", e.getMessage()));
            }
        }

        return new InvitedResponse(emails.size(), invited, skipped, failed, results);
    }

    @Transactional
    @Override
    public UserDetailsDTO profileComplete(ProfileCompleteDTO request) {
        User user = userRepository.findById(authUtil.loggedInUserIdForTesting())
                .orElseThrow(() -> new ResourceNotFoundException("user.notfound"));

        switch (request.getStep().toUpperCase()) {
            case "ONE" -> handleStepOne(user, request);
            case "TWO" -> handleStepTwo(user, request);
            case "THREE" -> handleStepThree(user);
            default -> throw new APIException("invalid.profile.complete.step", HttpStatus.BAD_REQUEST);
        }

        User savedUser = userRepository.save(user);
        return userMapper.toProfileCompleteDTO(savedUser);
    }

    @Transactional
    @Override
    public String followUnfollowUser(String userName) {
        User userNameExists = userRepository.findByUserName(userName)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username"));
        Long loggedInUserId = authUtil.loggedInUserIdForTesting();
        User senderUser = userRepository.findUserById(loggedInUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with userId"));
        if (userNameExists.getId().equals(loggedInUserId)) {
            throw new APIException("You cannot follow/unfollow yourself", HttpStatus.BAD_REQUEST);
        }
        Boolean existingFollow = userFollowRepository.existsByFollowerIdAndFollowingId(loggedInUserId, userNameExists.getId());
        if(existingFollow){
            userFollowRepository.deleteByFollowerIdAndFollowingId(loggedInUserId, userNameExists.getId());

            //  Send unfollow notification
            /*notificationService.sendNotification(loggedInUserId, targetUser.getId(), "User unfollowed you",
                    authUtil.getLoggedInUsername() + " has unfollowed you.", "UNFOLLOW", loggedInUserId);*/

            notificationService.sendNotification(
                    senderUser,
                    userNameExists,
                    "User Unfollowed",
                    authUtil.loggedInUserIdForTesting() + " has unfollowed you.",
                    SourceType.FOLLOW,
                    loggedInUserId
            );

            return "User unfollowed successfully";
        }else {
            User follower = userRepository.findById(loggedInUserId)
                    .orElseThrow(() -> new ResourceNotFoundException("Logged in user not found"));

            UserFollow follow = new UserFollow();
            follow.setFollower(follower);
            follow.setFollowing(userNameExists);
            userFollowRepository.save(follow);

            // Send follow notification
            /*notificationService.sendNotification(loggedInUserId, targetUser.getId(), "New Follower",
                    authUtil.getLoggedInUsername() + " started following you.", "FOLLOW", follow.getId());*/
            notificationService.sendNotification(
                    senderUser,
                    userNameExists,
                    "New Follower",
                    authUtil.loggedInUserIdForTesting() + " started following you.",
                    SourceType.FOLLOW,
                    follow.getId()
            );


            return "User followed successfully";
        }
    }

    @Override
    public ProfileVisitResponseDTO profileVisit(String userName) {
        Long currentUserId = authUtil.loggedInUserIdForTesting();
        User targetUser;
        boolean isSelf;

        if (userName == null || userName.isBlank()) {
            // Visiting own profile
            targetUser = userRepository.findById(currentUserId)
                    .orElseThrow(() -> new ResourceNotFoundException("user.notfound"));
            isSelf = true;
        } else {
            // Visiting someone else's profile
            targetUser = userRepository.findByUserName(userName)
                    .orElseThrow(() -> new ResourceNotFoundException("user.notfound"));
            isSelf = targetUser.getId().equals(currentUserId);
        }

        // Fetch current user details
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("user.notfound"));

        // Build profile DTO
        ProfileVisitDTO userDetailsDTO = userMapper.toUserProfileVisitDTO(targetUser);

        // Followers / Following count
        userDetailsDTO.setFollowersCount(userFollowRepository.countByFollowingId(targetUser.getId()));
        userDetailsDTO.setFollowingCount(userFollowRepository.countByFollowerId(targetUser.getId()));

        // For other user's profile, check relationship
        if (!isSelf) {
            boolean isFollowing = userFollowRepository.existsByFollowerIdAndFollowingId(currentUserId, targetUser.getId());
            boolean isFollower = userFollowRepository.existsByFollowerIdAndFollowingId(targetUser.getId(), currentUserId);
            userDetailsDTO.setIsFollowing(isFollowing);
            userDetailsDTO.setIsFollower(isFollower);
        }

        // Calculate access flags
        AccessFlags access = new AccessFlags();
        access.setCourseAccess(true);
        access.setGroupAccess(true);
        access.setShareProfileAccess(true);

        Long currentRole = currentUser.getRole().getId();
        Long targetRole = targetUser.getRole().getId();
        if (isSelf) {
            // ✅ Visiting own profile
            if (currentRole == 4L) { // Member
                access.setSaveContentAccess(true);
                access.setMyContentAccess(true);
                access.setLogoutAccess(true);
                access.setEditAccess(true);
            } else if (List.of(2L, 3L).contains(currentRole)) { // Admin or Teacher
                access.setLogoutAccess(true);
                access.setEditAccess(true);
                access.setArchiveAccess(true);
            }
        } else {
            // If current user is Admin (2) viewing Teacher (3) or Member (4)
            if (currentRole == 2L && List.of(3L, 4L).contains(targetRole)) {
                access.setChangeRoleAccess(true);
                access.setRemoveCommunityAccess(true);
                access.setBlockUserAccess(true);
                access.setReportUserAccess(true);
            }

            // If Admin or Teacher (2,3) viewing Member (4)
            if (List.of(2L, 3L).contains(currentRole) && targetRole == 4L) {
                access.setSaveContentAccess(true);
            }

            // If Admin (2) viewing Teacher (3)
            if (currentRole == 2L && targetRole == 3L) {
                access.setArchiveAccess(true);
            }
        }

        userDetailsDTO.setAccess(access);
        log.info("Final Access Flags before return: {}", userDetailsDTO.getAccess());

        // Build final response
        ProfileVisitResponseDTO response = new ProfileVisitResponseDTO();
        response.setUser(userDetailsDTO);

        return response;
    }

    @Transactional
    @Override
    public SuperAdminProfileResponseDTO superAdminProfile(String userName) {
        User userInfo = userRepository.findByUserName(userName)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username"));

        SuperAdminProfileResponseDTO response = userMapper.toSuperAminProfileDTO(userInfo);
        Integer InvitedUserCount = userRepository.findInvitedUserCount(userInfo.getId());
        response.setInvitedUserCount(InvitedUserCount);
        return response;
    }

    public ActivePendingUserDTO PendingActiveSuperAdmin(String type) {
        // TODO
        Long LoginUserId = authUtil.loggedInUserId();
        Optional<User> userInfo = userRepository.findById(LoginUserId);
        Long communityId = userInfo.get().getUserCommunity().getId();
        ProfileComplete status = type.equalsIgnoreCase("active") ? ProfileComplete.COMPLETE : ProfileComplete.PENDING;

        List<UserDetailsDTO> adminUsers = userRepository.findUserByRoleAndCommunityAndStatus(2L, communityId, status);
        List<UserDetailsDTO> teacherUsers = userRepository.findUserByRoleAndCommunityAndStatus(3L, communityId, status);
        List<UserDetailsDTO> memberUsers = userRepository.findUserByRoleAndCommunityAndStatus(4L, communityId, status);

        int totalCount = adminUsers.size() + teacherUsers.size() + memberUsers.size();
        //! Community details will be include into response
        return new ActivePendingUserDTO(adminUsers, teacherUsers, memberUsers, totalCount);
    }

    @Transactional
    @Override //!admin can change role of Teacher and Member .. Teacher only can change the Member role
    public void changeUserRole(String userName, Long role) {
        User userInfo = userRepository.findByUserName(userName).orElseThrow(()-> new ResourceNotFoundException("User not found"));
        Optional<Role> roleInfo = roleRepository.findById(role);
        userInfo.setRole(roleInfo.get());
        userRepository.save(userInfo);
    }

    @Override
    public ActivePendingUserDTO PendingActiveSuperAdmin(String type, Integer pageNumber, Integer pageSize, String sortOrder) {
        return null;
    }

    @Transactional
    @Override
    public Optional<User> findByEmail(String email) {
        log.info("Finding user by Email {}", email);
        return userRepository.findByEmail(email);
    }

    @Transactional
    @Override
    public void registerUser(User user) {
        if (user.getPassword() != null)
            user.setPassword(passwordEncoder.encode(user.getPassword()));
         userRepository.save(user);
    }

    @Override
    public ProfileVisitDTO userProfile() {
        Long loginUserId = authUtil.loggedInUserIdForTesting();
        User user = userRepository.findById(loginUserId).orElseThrow(()-> new ResourceNotFoundException("User not found"));
        return userMapper.toUserProfileVisitDTO(user);
    }

    @Override
    public ProfileVisitResponseDTO profileVisitTabs(String userName, String tabs, Integer pageNumber, Integer pageSize, String sortOrder) {
        User targetUser = userRepository.findByUserName(userName)
                .orElseThrow(() -> new ResourceNotFoundException("user.notfound"));
        Sort.Direction direction = "DESC".equalsIgnoreCase(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(pageNumber != null ? pageNumber : 0,
                pageSize != null ? pageSize : 10,
                Sort.by(direction, "createdAt"));

        Long roleId = targetUser.getRole().getId();
        Long communityId = authUtil.communityId();
        List<UserCourseResponseDTO> courses = new ArrayList<>();
        List<UserShortContentResponseDTO> shortContents = new ArrayList<>();
        List<UserGroupResponseDTO> groups = new ArrayList<>();

        // ====== 1️⃣ COURSE TAB ======
        if (tabs.contains("COURSE")) {
            if (roleId == 4L) {
                // Member: enrolled courses only
                courses = userHelperMethod.findEnrolledCoursesByUserId(targetUser.getId(), pageable);
            } else if (roleId == 3L) {
                // Teacher: courses created by this user
                courses = userHelperMethod.findCoursesCreatedByTeacherId(targetUser.getId(), pageable);
            } else if (roleId == 2L) {
                // Admin: all courses in the admin's community
                courses = userHelperMethod.findAllByCommunityId(communityId, pageable);
            }else {
                throw new APIException("Invalid.role.profile.tabs", HttpStatus.BAD_REQUEST);
            }
        }

        // ====== 2️⃣ CONTENT TAB ======
        if (tabs.contains("MYCONTENT")) {
            if (roleId == 4 ) {
                // Member: visible only his own created content
                shortContents = userHelperMethod.findVisibleToHisContent(targetUser.getId(), pageable);
            }
            /*else if (roleId == 3) {
                // Teacher: content created by this user
                shortContents = userHelperMethod.findByCreatedBy(targetUser.getId(), pageable);
            } else if (roleId == 2) {
                // Admin: all content in the community
                shortContents = userHelperMethod.findAllByCommunityId(communityId, pageable);
            }*/
        }

        if (tabs.contains("SAVECONTENT")) {
            if (roleId == 4 ) {
                // Member: visible or enrolled content
                shortContents = userHelperMethod.findVisibleToUser(targetUser.getId(), pageable);
            }
            /*else if (roleId == 3) {
                // Teacher: content created by this user
                shortContents = userHelperMethod.findByCreatedBy(targetUser.getId(), pageable);
            } else if (roleId == 2) {
                // Admin: all content in the community
                shortContents = userHelperMethod.findAllByCommunityId(communityId, pageable);
            }*/
        }

        // ====== 3️⃣ GROUP TAB ======
        if (tabs.contains("GROUP")) {
            if (roleId == 4) {
                // Member: groups joined by user
                groups = userHelperMethod.findGroupsByUserId(targetUser.getId(), pageable);
            } else if (roleId == 3) {
                // Teacher: groups created or join in by teacher
                groups = userHelperMethod.findGroupsCreatedByUser(targetUser.getId(), pageable);
            } else if (roleId == 2) {
                // Admin: all groups in community
                groups = userHelperMethod.findAllByCommunityIdForGroup(communityId, pageable);
            }else{
                throw new APIException("Invalid.role.profile.tabs", HttpStatus.BAD_REQUEST);
            }
        }

        // ====== BUILD RESPONSE ======
        ProfileVisitResponseDTO response = new ProfileVisitResponseDTO();
        response.setCourse(courses);
        response.setShortContent(shortContents);
        response.setGroup(groups);

        return response;
    }

    private void handleStepOne(User user, ProfileCompleteDTO request) {
        log.info("Profile Image: {}", request.getProfilePicture());
        if (request.getProfilePicture() == null) {
            throw new APIException("profile.image.required", HttpStatus.BAD_REQUEST);
        }

        if (request.getFirstName() == null || request.getLastName() == null
                || request.getCity() == null || request.getDob() == null) {
            throw new APIException("all.fields.required", HttpStatus.BAD_REQUEST );
        }
//        LocalDate dob;
//        try {
//            dob = LocalDate.parse(request.getDob());
//        } catch (DateTimeParseException e) {
//            throw new APIException("Invalid Date of Birth format. Use YYYY-MM-DD.");
//        }


        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setCity(request.getCity());
        user.setDob(request.getDob());
        user.setProfilePicture(request.getProfilePicture());
        user.setStep(Step.ONE);
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
    }

    private void handleStepTwo(User user, ProfileCompleteDTO request) {
        log.info("User request: {} {}", request.getInterest(), user.getId() );
        Long roleId = user.getRole().getId();
        if (roleId == 4) {
            if (request.getInterest() == null || request.getInterest().isEmpty()) {
                throw new APIException("member.interest.required", HttpStatus.BAD_REQUEST);
            }
            user.setInterest(request.getInterest());
        } else {
           throw new APIException("interest.not.required", HttpStatus.BAD_REQUEST);
        }

        user.setStep(Step.TWO);
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
    }

    private void handleStepThree(User user) {
        user.setStep(Step.THREE);
        user.setProfileComplete(ProfileComplete.COMPLETE);
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());

        NotificationPermission permission = notificationPermissionRepository
                .findByNotificationPermissionUser(user)
                .orElseGet(() -> {
                    NotificationPermission newPermission = new NotificationPermission();
                    newPermission.setNotificationPermissionUser(user);
                    newPermission.setEventNotification(true);
                    newPermission.setCourseNotification(true);
                    newPermission.setFollowNotification(true);
                    newPermission.setGroupNotification(true);
                    newPermission.setReplyNotification(true);
                    return notificationPermissionRepository.save(newPermission);
                });

        notificationPermissionRepository.save(permission);
    }

    private AccessFlags calculateAccess(User currentUser, User targetUser, boolean isSelf) {
        AccessFlags access = new AccessFlags();
        log.info("isSelf={}, currentRole={}, targetRole={}", isSelf,
                currentUser.getRole() != null ? currentUser.getRole().getId() : null,
                targetUser.getRole() != null ? targetUser.getRole().getId() : null);

        if (isSelf) {
            log.info("isSelf---- {}", isSelf);
            if (currentUser.getRole().getId() == 4) {
                access.setSaveContentAccess(true);
                access.setMyContentAccess(true);
                access.setLogoutAccess(true);
                access.setEditAccess(true);
            } else if (List.of(2, 3).contains(currentUser.getRole().getId())) {
                log.info("isSelf2---- {}", isSelf);
                access.setLogoutAccess(true);
                access.setEditAccess(true);
                access.setArchiveAccess(true);
            }
        } else {
            if (currentUser.getRole().getId() == 2 && List.of(3, 4).contains(targetUser.getRole().getId())) {
                access.setChangeRoleAccess(true);
                access.setRemoveCommunityAccess(true);
                access.setBlockUserAccess(true);
                access.setReportUserAccess(true);
            }
            if (List.of(2, 3).contains(currentUser.getRole().getId()) && targetUser.getRole().getId() == 4) {
                access.setSaveContentAccess(true);
            }
            if (currentUser.getRole().getId() == 2 && targetUser.getRole().getId() == 3) {
                access.setArchiveAccess(true);
            }
        }
        return access;
    }

}
