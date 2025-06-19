package org.cosmetic.com.service.impl;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.cosmetic.com.dto.request.LoginRequestDto;
import org.cosmetic.com.dto.request.RegisterRequestDto;
import org.cosmetic.com.dto.response.LoginResponseDto;
import org.cosmetic.com.enums.Role;
import org.cosmetic.com.exception.InvalidOtpException;
import org.cosmetic.com.exception.ResourceNotFoundException;
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
                .orElseThrow(() -> new ResourceNotFoundException("Email not found"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
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
            throw new RuntimeException("Invalid access token");
        }
        // Optionally, you can log the logout action or perform any other cleanup

    }

    @Override
    public void refreshToken(LoginResponseDto response) {

    }

    @Override
    public void register(RegisterRequestDto request) {
        Optional<User> user = userRepository.findByEmail(request.getEmail());
        // Check if user already exists
        if(user.isPresent() && user.get().isEnabled()) {
            throw new RuntimeException("Email already exists");
        }
        // If user exists but is not enabled
        if(user.isPresent() && !user.get().isEnabled()) {
            // Generate a new OTP and send it to the user's email
            String otp = otpService.generateOtpCode();
            emailService.sendVerificationEmail(user.get().getEmail(), otp);
            otpService.saveOtp(otp, user.get().getEmail());
            return;
        }
        User newUser =  User.builder()
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
        if(email == null) {
            throw new RuntimeException("Invalid OTP");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);
        return true;
    }

    @Override
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        String otp = otpService.generateOtpCode();
        emailService.sendForgotPasswordEmail(user.getEmail(), otp);
        otpService.saveOtp(otp, user.getEmail());
    }

    @Override
    public void resendVerificationEmail(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (user.isEnabled()) {
            throw new RuntimeException("User already verified");
        }
        String otp = otpService.generateOtpCode();
        emailService.sendVerificationEmail(user.getEmail(), otp);
        otpService.saveOtp(otp, user.getEmail());
    }
}
