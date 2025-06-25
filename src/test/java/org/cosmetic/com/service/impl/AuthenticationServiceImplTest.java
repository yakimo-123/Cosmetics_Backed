package org.cosmetic.com.service.impl;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.cosmetic.com.dto.request.LoginRequestDto;
import org.cosmetic.com.dto.request.RegisterRequestDto;
import org.cosmetic.com.dto.response.LoginResponseDto;
import org.cosmetic.com.enums.Role;
import org.cosmetic.com.exception.AppException;
import org.cosmetic.com.exception.ErrorCode;
import org.cosmetic.com.model.User;
import org.cosmetic.com.repository.UserRepository;
import org.cosmetic.com.security.jwt.JwtUtil;
import org.cosmetic.com.service.EmailService;
import org.cosmetic.com.service.OtpService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private UserRepository userRepository;
    @Mock
    private OtpService otpService;
    @Mock
    private EmailService emailService;
    @Mock
    private HttpServletResponse response;

    private AuthenticationServiceImpl authenticationService;

    @BeforeEach
    void setUp() {
        authenticationService = new AuthenticationServiceImpl(
                passwordEncoder, jwtUtil, userRepository, otpService, emailService);
    }

    @Nested
    @DisplayName("Authentication Tests")
    class AuthenticationTests {

        @Test
        @DisplayName("Should successfully authenticate user")
        void shouldAuthenticateSuccessfully() {
            // Given
            LoginRequestDto request = new LoginRequestDto();
            request.setEmail("test@example.com");
            request.setPassword("password");

            User user = User.builder()
                    .email("test@example.com")
                    .password("encoded_password")
                    .username("testuser")
                    .role(Role.USER)
                    .build();

            when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(true);
            when(jwtUtil.generateToken(user.getUsername(), user.getRole())).thenReturn("access_token");
            when(jwtUtil.generateRefreshToken(user.getUsername(), user.getRole())).thenReturn("refresh_token");

            // When
            LoginResponseDto responseDto = authenticationService.authenticate(request, response);

            // Then
            assertNotNull(responseDto);
            assertEquals("access_token", responseDto.getAccessToken());
            assertEquals(user.getUsername(), responseDto.getUsername());
            verify(response).addCookie(any(Cookie.class));
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void shouldThrowExceptionWhenUserNotFound() {
            // Given
            LoginRequestDto request = new LoginRequestDto();
            request.setEmail("nonexistent@example.com");

            when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

            // When & Then
            AppException exception = assertThrows(AppException.class,
                    () -> authenticationService.authenticate(request, response));
            assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("Registration Tests")
    class RegistrationTests {

        @Test
        @DisplayName("Should successfully register new user")
        void shouldRegisterNewUser() {
            // Given
            RegisterRequestDto request = new RegisterRequestDto();
            request.setEmail("new@example.com");
            request.setPassword("password");

            when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
            when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
            when(otpService.generateOtpCode()).thenReturn("123456");

            // When
            authenticationService.register(request);

            // Then
            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());
            User savedUser = userCaptor.getValue();
            assertEquals(request.getEmail(), savedUser.getEmail());
            assertEquals("encoded_password", savedUser.getPassword());
            assertEquals(Role.USER, savedUser.getRole());
            verify(emailService).sendVerificationEmail(eq(request.getEmail()), anyString());
        }

        @Test
        @DisplayName("Should throw exception when email already exists and user is enabled")
        void shouldThrowExceptionWhenEmailExists() {
            // Given
            RegisterRequestDto request = new RegisterRequestDto();
            request.setEmail("existing@example.com");

            User existingUser = User.builder()
                    .email("existing@example.com")
                    .enabled(true)
                    .build();

            when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(existingUser));

            // When & Then
            AppException exception = assertThrows(AppException.class,
                    () -> authenticationService.register(request));
            assertEquals(ErrorCode.EMAIL_ALREADY_EXISTS, exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("Email Verification Tests")
    class EmailVerificationTests {

        @Test
        @DisplayName("Should successfully verify email")
        void shouldVerifyEmailSuccessfully() {
            // Given
            String verificationCode = "123456";
            String email = "test@example.com";
            User user = User.builder()
                    .email(email)
                    .enabled(false)
                    .build();

            when(otpService.getEmailByOtp(verificationCode)).thenReturn(email);
            when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

            // When
            boolean result = authenticationService.verifyEmail(verificationCode);

            // Then
            assertTrue(result);
            assertTrue(user.isEnabled());
            verify(userRepository).save(user);
        }

        @Test
        @DisplayName("Should throw exception for invalid OTP")
        void shouldThrowExceptionForInvalidOtp() {
            // Given
            String invalidCode = "invalid";
            when(otpService.getEmailByOtp(invalidCode)).thenReturn(null);

            // When & Then
            AppException exception = assertThrows(AppException.class,
                    () -> authenticationService.verifyEmail(invalidCode));
            assertEquals(ErrorCode.INVALID_OTP, exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("Password Recovery Tests")
    class PasswordRecoveryTests {

        @Test
        @DisplayName("Should successfully process forgot password request")
        void shouldProcessForgotPasswordSuccessfully() {
            // Given
            String email = "test@example.com";
            User user = User.builder().email(email).build();
            String otp = "123456";

            when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
            when(otpService.generateOtpCode()).thenReturn(otp);

            // When
            authenticationService.forgotPassword(email);

            // Then
            verify(emailService).sendForgotPasswordEmail(email, otp);
            verify(otpService).saveOtp(otp, email);
        }
    }
}