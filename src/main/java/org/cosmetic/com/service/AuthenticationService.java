package org.cosmetic.com.service;

import jakarta.servlet.http.HttpServletResponse;
import org.cosmetic.com.dto.request.LoginRequestDto;
import org.cosmetic.com.dto.request.RegisterRequestDto;
import org.cosmetic.com.dto.response.LoginResponseDto;
import org.springframework.stereotype.Service;


@Service
public interface AuthenticationService {
    LoginResponseDto authenticate(LoginRequestDto request, HttpServletResponse response);
    void logout(String accessToken);
    void refreshToken(LoginResponseDto response);
    void register(RegisterRequestDto request);
    boolean verifyEmail(String email,String verificationCode);
}
