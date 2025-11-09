package com.maven.neuto.service;

import com.maven.neuto.payload.request.user.ForgetPassword;
import com.maven.neuto.payload.request.user.ForgetPasswordTokenVerifyDTO;
import com.maven.neuto.payload.request.user.LoginDTO;
import com.maven.neuto.payload.request.user.SignupDTO;
import com.maven.neuto.payload.response.user.LoginResponse;
import com.maven.neuto.payload.response.user.RefreshTokenResponseDTO;
import com.maven.neuto.payload.response.user.RegisterResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    LoginResponse userLogin(LoginDTO loginDTO, HttpServletRequest request);
    RefreshTokenResponseDTO refrshToken(String refreshTokenHeader, HttpServletRequest request);
    void requestForgetPassword(String email);
    void forgetPasswordTokenVerify(ForgetPasswordTokenVerifyDTO request);
    void forgetPassword(ForgetPassword request);
    RegisterResponse userRegistration(SignupDTO request, HttpServletRequest httpServletRequest);
    void resendOtp(String email);
}
