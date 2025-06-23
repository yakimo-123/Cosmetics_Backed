package org.cosmetic.com.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cosmetic.com.dto.request.LoginRequestDto;
import org.cosmetic.com.dto.request.RegisterRequestDto;
import org.cosmetic.com.enums.Role;
import org.cosmetic.com.model.User;
import org.cosmetic.com.repository.UserRepository;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String BASE_URL = "/api/auth";

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Should successfully register a new user")
    void shouldSuccessfullyRegisterUser() throws Exception {
        // Given
        RegisterRequestDto registerRequest = RegisterRequestDto.builder()
                .email("test@example.com")
                .password("Test123!")
                .fullName("Test User")
                .build();

        // When
        mockMvc.perform(post(BASE_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.data").isNotEmpty());

        // Verify user was saved in database
        User savedUser = userRepository.findByUsername("testuser").orElse(null);
        assertNotNull(savedUser);
        assertEquals(registerRequest.getEmail(), savedUser.getEmail());
        assertEquals(registerRequest.getFullName(), savedUser.getFullname());
        assertFalse(savedUser.isEnabled()); // User should not be enabled until email verification
    }

    @Test
    @DisplayName("Should successfully login user")
    void shouldSuccessfullyLoginUser() throws Exception {
        // Given
        String password = "Test123!";
        User user = User.builder()
                .username("testuser")
                .password(passwordEncoder.encode(password))
                .email("test@example.com")
                .fullname("Test User")
                .role(Role.USER)
                .enabled(true)
                .build();
        userRepository.save(user);

        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setPassword(password);

        // When
        mockMvc.perform(post(BASE_URL + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("User logged in successfully"))
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(cookie().exists("refreshToken"))
                .andReturn();
    }

    @Test
    @DisplayName("Should fail login with invalid credentials")
    void shouldFailLoginWithInvalidCredentials() throws Exception {
        // Given
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setEmail("nonexistent@gmail.com");
        loginRequest.setPassword("wrong");

        // When/Then
        mockMvc.perform(post(BASE_URL + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should successfully logout user")
    void shouldSuccessfullyLogoutUser() throws Exception {
        // Given
        String accessToken = "Bearer valid_token"; // You might need to generate a real token

        // When/Then
        mockMvc.perform(post(BASE_URL + "/logout")
                .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("User logged out successfully"))
                .andExpect(cookie().value("refreshToken", (Matcher<? super String>) null))
                .andExpect(cookie().maxAge("refreshToken", 0));
    }

    @Test
    @DisplayName("Should successfully verify email")
    void shouldSuccessfullyVerifyEmail() throws Exception {
        // Given
        String verificationCode = "valid_code"; // You might need to generate a real code
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .enabled(false)
                .build();
        userRepository.save(user);

        // When/Then
        mockMvc.perform(get(BASE_URL + "/verify-email")
                .param("code", verificationCode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Email verified successfully"));
    }

    @Test
    @DisplayName("Should successfully request password reset")
    void shouldSuccessfullyRequestPasswordReset() throws Exception {
        // Given
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .enabled(true)
                .build();
        userRepository.save(user);

        // When/Then
        mockMvc.perform(post(BASE_URL + "/forgot-password")
                .param("email", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Password reset link sent successfully"));
    }
}