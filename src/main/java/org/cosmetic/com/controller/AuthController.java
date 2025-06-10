package org.cosmetic.com.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.cosmetic.com.dto.request.EmailVerificationRequest;
import org.cosmetic.com.dto.request.LoginRequestDto;
import org.cosmetic.com.dto.request.RegisterRequestDto;
import org.cosmetic.com.dto.response.ApiResponse;
import org.cosmetic.com.dto.response.LoginResponseDto;
import org.cosmetic.com.enums.Role;
import org.cosmetic.com.security.jwt.JwtUtil;
import org.cosmetic.com.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        LoginResponseDto loginResponse = authenticationService.authenticate(loginRequest,response);
        return ResponseEntity.ok(
                ApiResponse.<LoginResponseDto>builder()
                        .status(true)
                        .message("User logged in successfully")
                        .data(loginResponse)
                        .build()
        );
    }
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setHttpOnly(true);
       // cookie.setSecure(true);  use this if your application is served over HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(0); // Delete cookie
        response.addCookie(cookie);
        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .status(true)
                        .message("User logged out successfully")
                        .data("Logout successful")
                        .build()
        );
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<?>> refreshToken(@CookieValue(value = "refreshToken", required = false) String refreshToken) {
        if (refreshToken == null) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.<String>builder()
                            .status(false)
                            .message("Unauthorized")
                            .data("Refresh token is missing")
                            .build());
        }
        if (!jwtUtil.validateToken(refreshToken)) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.<String>builder()
                            .status(false)
                            .message("Unauthorized")
                            .data("Invalid refresh token")
                            .build());
        }
        String username = jwtUtil.getUsernameFromToken(refreshToken);
        Role role =Role.valueOf(jwtUtil.getRoleFromToken(refreshToken));
        String newAccessToken = jwtUtil.generateToken(username,role);
        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .status(true)
                        .message("Token refreshed successfully")
                        .data(newAccessToken)
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
                        .data("Registration successful, please check your email for verification")
                        .build()
        );
    }

    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse<String>> verifyEmail(
            @RequestBody EmailVerificationRequest emailVerificationRequest
            ) {
        boolean isVerified = authenticationService.verifyEmail(emailVerificationRequest.getEmail(), emailVerificationRequest.getOtp());
        if (isVerified) {
            return ResponseEntity.ok(
                    ApiResponse.<String>builder()
                            .status(true)
                            .message("Email verified successfully")
                            .data("Email verification successful")
                            .build()
            );
        } else {
            return ResponseEntity.status(400)
                    .body(ApiResponse.<String>builder()
                            .status(false)
                            .message("Email verification failed")
                            .data("Invalid or expired token")
                            .build()
                    );
        }
    }
}