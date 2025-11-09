package com.maven.neuto.serviceImplement;

import com.maven.neuto.config.MessageConfig;
import com.maven.neuto.exception.APIException;
import com.maven.neuto.emun.ProfileComplete;
import com.maven.neuto.emun.Status;
import com.maven.neuto.exception.ResourceNotFoundException;
import com.maven.neuto.mapstruct.UserMapper;
import com.maven.neuto.model.*;
import com.maven.neuto.payload.request.user.ForgetPassword;
import com.maven.neuto.payload.request.user.ForgetPasswordTokenVerifyDTO;
import com.maven.neuto.payload.request.user.LoginDTO;
import com.maven.neuto.payload.request.user.SignupDTO;
import com.maven.neuto.payload.response.user.*;
import com.maven.neuto.repository.*;
import com.maven.neuto.security.jwt.JwtUtils;
import com.maven.neuto.security.service.UserDetailsImpl;
import com.maven.neuto.service.AuthService;
import com.maven.neuto.utils.EmailService;
import com.maven.neuto.utils.HelperMethod;
import com.maven.neuto.utils.TemplateHelper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImple implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CommunityRepository communityRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpVerifyRepository otpVerifyRepository;
    private final HelperMethod helperMethod;
    private final MessageConfig messageConfig;
    private final EmailService emailService;
    private final PasswordResetRepository passwordResetRepository;
    private final TemplateHelper templateHelper;



    @Value("${spring.app.jwtRefreshExpirationMs}")
    private int jwtRefreshExpirationMs;

    @Override
    public LoginResponse userLogin(LoginDTO loginDTO, HttpServletRequest request) {
        String acceptLanguage = request.getHeader("Accept-Language");
        Locale locale = acceptLanguage != null ? Locale.forLanguageTag(acceptLanguage) : Locale.ENGLISH;

        User userStatusCheck = userRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException(messageConfig.getMessage("user.notfound", null, locale)
                ));
            log.info("userStatusCheck object {} {} {}", userStatusCheck.getEmail(), userStatusCheck.getPassword(), userStatusCheck.getStatus());
        if (userStatusCheck.getStatus() == Status.INACTIVE) {
            throw new APIException(messageConfig.getMessage("user.inactive", null, locale), HttpStatus.BAD_REQUEST);

        } else if (userStatusCheck.getStatus() == Status.DEACTIVATED) {
            throw new APIException(messageConfig.getMessage("user.deactivated", null, locale), HttpStatus.BAD_REQUEST);
        }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword())
            );
            log.info("Authenticaton Object {}", authentication);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        log.info("userDetails object {}", userDetails);
        String accessToken = jwtUtils.generateAccessToken(userDetails.getId());
        String refreshToken = jwtUtils.generateRefreshToken(userDetails.getId());
        String userAgentHeader = request.getHeader("User-Agent");
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        String userAgent = HelperMethod.getBrowserName(userAgentHeader);

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new UsernameNotFoundException(messageConfig.getMessage("user.notfound", null, locale)));
        user.setFcmToken(loginDTO.getFcmToken());
        userRepository.save(user);


        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setUserId(user.getId()); // Only user id will be store
        refreshTokenEntity.setRefreshToken(refreshToken);
        refreshTokenEntity.setUserAgent(userAgent);
        refreshTokenEntity.setIpaddress(ipAddress);
        refreshTokenEntity.setExpiresAt(LocalDateTime.now().plus(Duration.ofMillis(jwtRefreshExpirationMs)));
        refreshTokenRepository.save(refreshTokenEntity);

        log.info("Community User {}", user.getCommunityCreatedUser());
        Community community = null;
        if(user.getCommunityCreatedUser() != null){
            community = communityRepository.findByUserId(user.getCommunityCreatedUser().getId());
        }
        CommunityLoginResgistrationDTO communityDTOs =
                userMapper.toCommunityLoginResgistrationDTOs(community);


        return new LoginResponse(accessToken, refreshToken, userMapper.toUserDetailsDTO(user), communityDTOs);

    }

    @Transactional
    @Override
    public RefreshTokenResponseDTO  refrshToken(String refreshTokenHeader, HttpServletRequest request) {
        String refreshToken = refreshTokenHeader.substring(7);
        RefreshToken storedToken = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new ResourceNotFoundException("token.invalid"));
        if (!jwtUtils.validateJwtToken(refreshToken, true)) {
            throw new APIException("token.expired", HttpStatus.UNAUTHORIZED);
        }

        // ✅ Check Expiry
        if (storedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.deleteByUserId(storedToken.getUserId()); // Remove expired token
            throw new APIException("token.expired", HttpStatus.UNAUTHORIZED);
        }

        String newAccessToken = jwtUtils.generateAccessToken(storedToken.getUserId());
        String newRefreshToken = jwtUtils.generateRefreshToken(storedToken.getUserId());
        String userAgentHeader = request.getHeader("User-Agent");
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        String userAgent = HelperMethod.getBrowserName(userAgentHeader);

        // ✅ Update refresh token in DB
        storedToken.setRefreshToken(newRefreshToken);
        storedToken.setUserId(storedToken.getUserId());
        storedToken.setUserAgent(userAgent);
        storedToken.setIpaddress(ipAddress);
        storedToken.setExpiresAt(LocalDateTime.now().plus(Duration.ofMillis(jwtRefreshExpirationMs))); // Example: 7 days expiry
        refreshTokenRepository.save(storedToken);

        Locale locale = Locale.forLanguageTag(request.getHeader("Accept-Language"));
        String localizedMessage = messageConfig.getMessage("token.refreshed", null, locale);
        return new RefreshTokenResponseDTO(newAccessToken, newRefreshToken, localizedMessage);
    }

    @Transactional
    @Override
    public void requestForgetPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("user.notfound"));
        if(user.getStatus() == Status.INACTIVE){
            throw new APIException("user.inactive", HttpStatus.BAD_REQUEST);
        } else if (user.getStatus() == Status.DEACTIVATED) {
            throw new APIException("user.deactivated", HttpStatus.BAD_REQUEST);
        }

        String subject = "Forget Password Request Link Sent";
        String encryptedEmail = helperMethod.encryptEmail(user.getEmail().trim());
        String unsubscribeLink = "https://yourdomain.com/unsubscribe?email=" + encryptedEmail;
        String token = helperMethod.randomTokenGenerator(16);
        String resetLink = "https://yourdomain.com/reset-password?token=" + token;

        // Save to DB
        PasswordReset passwordReset = new PasswordReset();
        passwordReset.setEmail(user.getEmail());
        passwordReset.setToken(token);
        passwordReset.setExpiryDate(LocalDateTime.now().plusHours(24)); // 24 hours expiry
        passwordResetRepository.save(passwordReset);

        Map<String, Object> model = Map.of(
                "name", user.getFirstName(),
                "resetLink", resetLink,
                "unsubscribeLink", unsubscribeLink,
                "subject", subject,
                "weburl", "https://yourdomain.com",
                "year", Year.now().getValue(),
                "userAccess", true
        );

        try {
            String htmlContent = templateHelper.buildEmail("forgot-password", model);
            emailService.sendEmail(user.getEmail(), subject, htmlContent);
        } catch (Exception e) {
            log.error("Failed to send password reset email: {}", e);
            throw new APIException("send.email.failed", HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    @Override
    public void forgetPasswordTokenVerify(ForgetPasswordTokenVerifyDTO request) {
        PasswordReset passwordRecord = passwordResetRepository.findByToken(request.getToken())
                .orElseThrow(() -> new ResourceNotFoundException("password.token.invalid"));
        if (passwordRecord.getExpiryDate().isBefore(LocalDateTime.now())) {
            passwordResetRepository.delete(passwordRecord);
            throw new APIException("password.token.expired", HttpStatus.BAD_REQUEST);
        }
        User user = userRepository.findByEmail(passwordRecord.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("user.notfound"));
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        user.setPassword(encodedPassword);
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);
        String encryptedEmail = helperMethod.encryptEmail(user.getEmail().trim());
        String unsubscribeLink = "https://yourdomain.com/unsubscribe?email=" + encryptedEmail;
        String subject = "Password Reset Complete – You’re Good to Go!";

        Map<String, Object> model = Map.of(
                "name", user.getFirstName(),
                "unsubscribeLink", unsubscribeLink,
                "subject", subject,
                "weburl", "https://yourdomain.com",
                "year", Year.now().getValue(),
                "userAccess", true
        );

        try {
            String htmlContent = templateHelper.buildEmail("success-password-changed", model);
            emailService.sendEmail(user.getEmail(), subject, htmlContent);
        } catch (Exception e) {
            log.error("Failed to send success email to {}: {}", user.getEmail(), e.getMessage());
        }
        passwordResetRepository.delete(passwordRecord);
    }

    @Transactional
    @Override
    public void forgetPassword(ForgetPassword request) {
        Otpverify otpRecord = otpVerifyRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("OTP verification required"));

        if (!otpRecord.isVerified()) {
            throw new APIException("OTP is not verified", HttpStatus.BAD_REQUEST);
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // OTP should not be reused
        otpVerifyRepository.delete(otpRecord);
    }

    @Transactional
    @Override
    public RegisterResponse userRegistration(SignupDTO request, HttpServletRequest httpServletRequest) {
        User existingUser = userRepository.findByEmail(request.getEmail()).orElseThrow(()-> new ResourceNotFoundException("user.notfound"));
        if (existingUser.getStatus() == Status.ACTIVE) {
            throw new APIException("user.already.exists", HttpStatus.BAD_REQUEST);
        } else if (existingUser.getStatus() == Status.DEACTIVATED) {
            throw new APIException("user.deactivated", HttpStatus.BAD_REQUEST);
        }


        // 4. (Optional) Check invitation expiry (if applicable)
        /*
        if (invitation.getExpiredAt() != null && LocalDateTime.now().isAfter(invitation.getExpiredAt())) {
            throw new ApiException("Your invitation has expired");
        }
        */

        // 5. Encrypt password
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // 6. Updated user
        existingUser.setEmail(request.getEmail());
        existingUser.setPassword(hashedPassword);
        existingUser.setStatus(Status.ACTIVE);
        existingUser.setProfileComplete(ProfileComplete.PENDING);
        existingUser.setFcmToken(request.getFcmToken());
        userRepository.save(existingUser);

        String accessToken = jwtUtils.generateAccessToken(existingUser.getId());
        String refreshToken = jwtUtils.generateRefreshToken(existingUser.getId());
        String userAgentHeader = httpServletRequest.getHeader("User-Agent");
        String ipAddress = httpServletRequest.getHeader("X-Forwarded-For");
        if (ipAddress == null) {
            ipAddress = httpServletRequest.getRemoteAddr();
        }
        String userAgent = HelperMethod.getBrowserName(userAgentHeader);
        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setUserId(existingUser.getId());
        refreshTokenEntity.setRefreshToken(refreshToken);
        refreshTokenEntity.setUserAgent(userAgent);
        refreshTokenEntity.setIpaddress(ipAddress);
        refreshTokenEntity.setExpiresAt(LocalDateTime.now().plus(Duration.ofMinutes(jwtRefreshExpirationMs)));
        refreshTokenRepository.save(refreshTokenEntity);

        return new RegisterResponse(accessToken, refreshToken, userMapper.toUserDetailsDTO(existingUser));
    }

    @Transactional
    @Override
    public void resendOtp(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(()-> new ResourceNotFoundException("User not found"));
        String subject = "Forget Password Request";
        String htmlTemplate = "";
        helperMethod.handleOtpProcess(user, subject, htmlTemplate);
    }


}
