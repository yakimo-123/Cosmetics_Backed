package org.cosmetic.com.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cosmetic.com.dto.request.LoginRequestDto;
import org.cosmetic.com.dto.request.RegisterRequestDto;
import org.cosmetic.com.enums.Role;
import org.cosmetic.com.model.User;
import org.cosmetic.com.repository.UserRepository;
import org.cosmetic.com.service.EmailService;
import org.cosmetic.com.service.OtpService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("AuthController Integration Tests")
class AuthControllerIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserRepository userRepository;

    @MockitoBean EmailService emailService;
    @MockitoBean OtpService otpService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Nested
    @DisplayName("Register Flow")
    class RegisterFlow {
        RegisterRequestDto request;

        @BeforeEach
        void init() {
            request = RegisterRequestDto.builder()
                    .email("alice@example.com")
                    .password("StrongPass#1")
                    .fullName("Alice Wonderland")
                    .build();
            when(otpService.generateOtpCode()).thenReturn("654321");
        }

        @Test
        @DisplayName("200 OK – creates inactive user and sends OTP e-mail")
        void newUser_success() throws Exception {
            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(true))
                    .andExpect(jsonPath("$.message").value("User registered successfully"));

            User saved = userRepository.findByEmail("alice@example.com").orElseThrow();
            assertThat(saved.getFullname()).isEqualTo("Alice Wonderland");
            assertThat(saved.getRole()).isEqualTo(Role.USER);
            assertThat(saved.isEnabled()).isFalse();

            verify(otpService).generateOtpCode();
            verify(emailService).sendVerificationEmail("alice@example.com", "654321");
            verify(otpService).saveOtp("654321", "alice@example.com");
            verifyNoMoreInteractions(emailService, otpService);
        }

        @Test
        @DisplayName("200 OK – existing, not-enabled user receives new OTP")
        void existingNotEnabled_resendsOtp() throws Exception {
            userRepository.save(User.builder()
                    .username("alice")
                    .password("dummy")
                    .fullname("Alice Wonderland")
                    .email("alice@example.com")
                    .role(Role.USER)
                    .enabled(false)
                    .build());

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            verify(otpService).generateOtpCode();
            verify(emailService).sendVerificationEmail("alice@example.com", "654321");
            verify(otpService).saveOtp("654321", "alice@example.com");
            verifyNoMoreInteractions(emailService, otpService);
        }

        @Test
        @DisplayName("400 Bad-Request – e-mail already taken")
        void duplicateEmail_enabledUser() throws Exception {
            userRepository.save(User.builder()
                    .username("alice")
                    .password("dummy")
                    .fullname("Alice Wonderland")
                    .email("alice@example.com")
                    .role(Role.USER)
                    .enabled(true)
                    .build());

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(false))
                    .andExpect(jsonPath("$.message").value("Email already exists"));

            verifyNoInteractions(emailService, otpService);
        }
    }

    @Nested
    @DisplayName("Login Flow")
    class LoginFlow {

        @BeforeEach
        void setUpUser() {
            User user = User.builder()
                    .username("testuser")
                    .password(passwordEncoder.encode("Test123!"))
                    .fullname("Test User")
                    .email("login@example.com")
                    .role(Role.USER)
                    .enabled(true)
                    .build();
            userRepository.save(user);
        }

        @Test
        @DisplayName("200 OK – login success returns access token")
        void login_success() throws Exception {
            LoginRequestDto loginRequest = LoginRequestDto.builder()
                    .email("login@example.com")
                    .password("Test123!")
                    .build();

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(true))
                    .andExpect(jsonPath("$.message").value("User logged in successfully"))
                    .andExpect(jsonPath("$.data.accessToken").exists())
                    .andExpect(jsonPath("$.data.username").value("testuser"));
        }

        @Test
        @DisplayName("401 Unauthorized – wrong password")
        void login_wrongPassword() throws Exception {
            LoginRequestDto loginRequest = LoginRequestDto.builder()
                    .email("login@example.com")
                    .password("WrongPass!")
                    .build();

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.status").value(false))
                    .andExpect(jsonPath("$.message").value("Invalid email or password"));
        }
    }
}
