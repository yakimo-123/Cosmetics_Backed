package org.cosmetic.com.security.oath2;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.cosmetic.com.dto.response.ApiResponse;
import org.cosmetic.com.dto.response.LoginResponseDto;
import org.cosmetic.com.security.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component

public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    @Value("${cookie.expiration}")
    private int cookieExpiration;

    public CustomOAuth2SuccessHandler(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String username = authentication.getName();
        String accessToken  = jwtUtil.generateToken(username);
        String refreshToken  = jwtUtil.generateRefreshToken(username);

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
