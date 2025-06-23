package org.cosmetic.com.service.impl;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.cosmetic.com.dto.request.LoginRequestDto;
import org.cosmetic.com.dto.request.RegisterRequestDto;
import org.cosmetic.com.dto.response.LoginResponseDto;
import org.cosmetic.com.enums.Role;
import org.cosmetic.com.exception.*;
import org.cosmetic.com.model.User;
import org.cosmetic.com.repository.UserRepository;
import org.cosmetic.com.security.jwt.JwtUtil;
import org.cosmetic.com.service.AuthenticationService;
import org.cosmetic.com.service.EmailService;
import org.cosmetic.com.service.OtpService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;


@AllArgsConstructor
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final OtpService otpService;
    private final EmailService emailService;

    // This method is supposed to authenticate a user and return a LoginResponseDto
    @Override
    public LoginResponseDto authenticate(LoginRequestDto request, HttpServletResponse response) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }
        Cookie cookie = new Cookie("refreshToken", jwtUtil.generateRefreshToken(user.getUsername(), user.getRole()));
        cookie.setHttpOnly(true);
        // cookie.setSecure(true);  use this if your application is served over HTTPS
        cookie.setPath("/");
        response.addCookie(cookie);

        return LoginResponseDto.builder()
                .accessToken(jwtUtil.generateToken(user.getUsername(),user.getRole()))
                .username(user.getUsername())
                .build();
    }

    @Override
    public void logout(String accessToken) {
        // Invalidate the access token if necessary
        // This could involve removing it from a cache or database if you're tracking active sessions
        // For stateless JWT, you might not need to do anything here
        if (accessToken == null || accessToken.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_ACCESS_TOKEN);
        }
        // Optionally, you can log the logout action or perform any other cleanup

    }

    @Override
    public String refreshAccessToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
        // Validate the refresh token and generate a new access token
        String username = jwtUtil.getUsernameFromToken(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return jwtUtil.generateToken(user.getUsername(), user.getRole());
    }

    @Override
    public void register(RegisterRequestDto request) {
        Optional<User> user = userRepository.findByEmail(request.getEmail());
        // Check if user already exists
        if (user.isPresent() && user.get().isEnabled()) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        // If user exists but is not enabled
        if(user.isPresent()) {
            String otp = otpService.generateOtpCode();
            emailService.sendVerificationEmail(user.get().getEmail(), otp);
            otpService.saveOtp(otp, user.get().getEmail());
            return;
        }
        User newUser =  User.builder()
                .fullname(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .username(UUID.randomUUID().toString())
                .build();
        userRepository.save(newUser);
        String otp = otpService.generateOtpCode();
        // Send OTP to user's email
        emailService.sendVerificationEmail(newUser.getEmail(), otp);
        // Save OTP in the cache
        otpService.saveOtp(otp, newUser.getEmail());
    }

    @Override
    public boolean verifyEmail(String verificationCode) {

        String email = otpService.getEmailByOtp(verificationCode);
        if (email == null) {
            throw new AppException(ErrorCode.INVALID_OTP);
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        user.setEnabled(true);
        userRepository.save(user);
        return true;
    }

    @Override
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        String otp = otpService.generateOtpCode();
        emailService.sendForgotPasswordEmail(user.getEmail(), otp);
        otpService.saveOtp(otp, user.getEmail());
    }

    @Override
    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (user.isEnabled()) {
            throw new AppException(ErrorCode.USER_ALREADY_VERIFIED);
        }
        String otp = otpService.generateOtpCode();
        emailService.sendVerificationEmail(user.getEmail(), otp);
        otpService.saveOtp(otp, user.getEmail());
    }

}
