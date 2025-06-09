package org.cosmetic.com.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.cosmetic.com.dto.request.LoginRequestDto;
import org.cosmetic.com.dto.request.RegisterRequestDto;
import org.cosmetic.com.dto.response.ApiResponse;
import org.cosmetic.com.dto.response.LoginResponseDto;
import org.cosmetic.com.security.jwt.JwtUtil;
import org.cosmetic.com.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(
            @Valid @RequestBody LoginRequestDto loginRequest,
            HttpServletResponse response
    ) {
        LoginResponseDto loginResponse = authenticationService.authenticate(loginRequest);
        if (loginResponse.getUsername() != null) {
            Cookie cookie = new Cookie("refreshToken", jwtUtil.generateRefreshToken(loginResponse.getUsername()));
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            response.addCookie(cookie);
        }
        return ResponseEntity.ok(
                ApiResponse.<LoginResponseDto>builder()
                        .status(true)
                        .message("User logged in successfully")
                        .data(loginResponse)
                        .build()
        );
    }
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout() {
        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .status(true)
                        .message("User logged out successfully")
                        .data("Logout successful")
                        .build()
        );
    }
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegisterRequestDto registerRequest) {
        authenticationService.register(registerRequest);
        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .status(true)
                        .message("User registered successfully")
                        .data("Registration successful")
                        .build()
        );
    }
}