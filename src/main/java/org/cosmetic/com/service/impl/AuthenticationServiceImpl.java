package org.cosmetic.com.service.impl;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.cosmetic.com.dto.request.LoginRequestDto;
import org.cosmetic.com.dto.request.RegisterRequestDto;
import org.cosmetic.com.dto.response.LoginResponseDto;
import org.cosmetic.com.enums.Role;
import org.cosmetic.com.model.User;
import org.cosmetic.com.repository.UserRepository;
import org.cosmetic.com.security.jwt.JwtUtil;
import org.cosmetic.com.service.AuthenticationService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@AllArgsConstructor
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    // This method is supposed to authenticate a user and return a LoginResponseDto
    @Override
    public LoginResponseDto authenticate(LoginRequestDto request, HttpServletResponse response) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
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
    public void logout(LoginResponseDto response) {

    }

    @Override
    public void refreshToken(LoginResponseDto response) {

    }

    @Override
    public void register(RegisterRequestDto request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setRole(Role.USER);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
    }
}
