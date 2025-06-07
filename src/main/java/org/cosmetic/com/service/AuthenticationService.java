package org.cosmetic.com.service;

import org.cosmetic.com.dto.request.LoginRequestDto;
import org.cosmetic.com.dto.request.RegisterRequestDto;
import org.cosmetic.com.dto.response.LoginResponseDto;
import org.springframework.stereotype.Service;


@Service
public interface AuthenticationService {
    LoginResponseDto authenticate(LoginRequestDto request);
    void logout(LoginResponseDto response);
    void refreshToken(LoginResponseDto response);
    void register(RegisterRequestDto request);
}
