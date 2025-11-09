package com.maven.neuto.controller;


import com.maven.neuto.config.MessageConfig;
import com.maven.neuto.payload.request.user.ForgetPassword;
import com.maven.neuto.payload.request.user.ForgetPasswordTokenVerifyDTO;
import com.maven.neuto.payload.request.user.LoginDTO;
import com.maven.neuto.payload.request.user.SignupDTO;
import com.maven.neuto.payload.response.user.LoginResponse;
import com.maven.neuto.payload.response.user.RefreshTokenResponseDTO;
import com.maven.neuto.payload.response.user.RegisterResponse;
import com.maven.neuto.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Locale;


@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final MessageConfig messageConfig;

    @GetMapping("/user-testing")
    public ResponseEntity<?> testing() {
        return ResponseEntity.ok("Application running successfully!");
    }

    @PostMapping("/user-login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginDTO loginDTO, HttpServletRequest request) {
        LoginResponse response = authService.userLogin(loginDTO,  request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestHeader("Authorization") String refreshTokenHeader, HttpServletRequest request) {
        RefreshTokenResponseDTO tokens = authService.refrshToken(refreshTokenHeader, request);
        return new ResponseEntity<>(tokens, HttpStatus.OK);
    }

    @PostMapping("/request-forget-password")
    public ResponseEntity<?> requestForgetPassword(@Valid @RequestParam("email") String email, Locale locale) {
        authService.requestForgetPassword(email);
        String localizedMessage = messageConfig.getMessage("request.forget.password", null, locale);
        return ResponseEntity.ok(localizedMessage);
    }

    @PostMapping("/forget-password-token-verify")
    public ResponseEntity<?> forgetPasswordTokenVerify(@Valid @RequestBody ForgetPasswordTokenVerifyDTO request, Locale locale) {
        authService.forgetPasswordTokenVerify(request);
        String localizedMessage = messageConfig.getMessage("forget.password.change.successful", null, locale);
        return ResponseEntity.ok(localizedMessage);
    }

    //! Not Required
    @PatchMapping("/forget-password")
    public ResponseEntity<?> forgetPassword(@Valid @RequestBody ForgetPassword request){
        authService.forgetPassword(request);
        return ResponseEntity.ok("password successfully change.");
    }

    @PostMapping("/user-register")
    public ResponseEntity<?> userRegister(@Valid @RequestBody SignupDTO request, HttpServletRequest httpServletRequest){
        RegisterResponse response = authService.userRegistration(request, httpServletRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOtp(@Valid @RequestBody String email){
        authService.resendOtp(email);
        return ResponseEntity.ok("Otp successfully send!");
    }

}
