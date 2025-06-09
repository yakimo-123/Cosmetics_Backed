package org.cosmetic.com.security.oath2;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.cosmetic.com.dto.response.ApiResponse;
import org.cosmetic.com.dto.response.LoginResponseDto;
import org.cosmetic.com.enums.Role;
import org.cosmetic.com.model.User;
import org.cosmetic.com.repository.UserRepository;
import org.cosmetic.com.security.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component

public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    private final UserRepository userRepository;


    @Value("${cookie.expiration}")
    private int cookieExpiration;

    public CustomOAuth2SuccessHandler(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oauthUser = oauthToken.getPrincipal();
        String email = oauthUser.getAttribute("email");
        String username = oauthUser.getAttribute("name");

        // Check if the user already exists in the database
        User user = userRepository.findByEmail(email);
        if (user == null) {
            // If the user does not exist, create a new user
            user = new User();
            user.setFullname(username);
            user.setEmail(email);
            user.setRole(Role.USER);
            user.setPassword("Oath2User"); // Set a default password or handle it as per your requirement
            userRepository.save(user);
        }
        Role role = user.getRole();


        String accessToken  = jwtUtil.generateToken(username, role);
        String refreshToken  = jwtUtil.generateRefreshToken(username,role);

        ApiResponse<LoginResponseDto> responseDtoApiResponse = ApiResponse.<LoginResponseDto>builder()
                .status(true)
                .message("User logged in successfully")
                .data(LoginResponseDto.builder()
                        .accessToken(accessToken)
                        .username(username)
                        .build())
                .build();

        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        // cookie.setSecure(true);  // Uncomment this if your application is served over HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(cookieExpiration);
        response.addCookie(cookie);

        ObjectMapper objectMapper = new ObjectMapper();
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(objectMapper.writeValueAsString(responseDtoApiResponse));
    }
}
