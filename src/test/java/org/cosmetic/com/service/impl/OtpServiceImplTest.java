package org.cosmetic.com.service.impl;

import org.cosmetic.com.exception.AppException;
import org.cosmetic.com.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OtpServiceImplTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private OtpServiceImpl otpService;

    @Nested
    @DisplayName("Save OTP Tests")
    class SaveOtpTests {

        @Test
        @DisplayName("Should successfully save OTP")
        void shouldSuccessfullySaveOtp() {
            // Given
            String otp = "123456";
            String email = "test@example.com";
            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            doNothing().when(valueOperations).set(anyString(), anyString(), any(Duration.class));

            // When
            assertDoesNotThrow(() -> otpService.saveOtp(otp, email));

            // Then
            verify(redisTemplate).opsForValue();
            verify(valueOperations).set(eq(otp), eq(email), any(Duration.class));
        }
    }

    @Nested
    @DisplayName("Verify OTP Tests")
    class VerifyOtpTests {

        @Test
        @DisplayName("Should successfully verify valid OTP")
        void shouldSuccessfullyVerifyValidOtp() {
            // Given
            String otp = "123456";
            String email = "test@example.com";
            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get(otp)).thenReturn(email);
            when(redisTemplate.delete(otp)).thenReturn(true);

            // When
            String result = otpService.verifyOtp(otp);

            // Then
            assertEquals(email, result);
            verify(redisTemplate).opsForValue();
            verify(valueOperations).get(otp);
            verify(redisTemplate).delete(otp);
        }

        @Test
        @DisplayName("Should throw exception for invalid OTP")
        void shouldThrowExceptionForInvalidOtp() {
            // Given
            String otp = "123456";
            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get(otp)).thenReturn(null);

            // When & Then
            AppException exception = assertThrows(AppException.class,
                    () -> otpService.verifyOtp(otp));
            assertEquals(ErrorCode.OTP_INVALID, exception.getErrorCode());
            verify(redisTemplate).opsForValue();
            verify(valueOperations).get(otp);
            verify(redisTemplate, never()).delete(anyString());
        }
    }

    @Nested
    @DisplayName("Get Email By OTP Tests")
    class GetEmailByOtpTests {

        @Test
        @DisplayName("Should successfully get email for valid OTP")
        void shouldSuccessfullyGetEmailForValidOtp() {
            // Given
            String otp = "123456";
            String email = "test@example.com";
            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get(otp)).thenReturn(email);

            // When
            String result = otpService.getEmailByOtp(otp);

            // Then
            assertEquals(email, result);
            verify(redisTemplate).opsForValue();
            verify(valueOperations).get(otp);
        }

        @Test
        @DisplayName("Should throw exception for invalid OTP when getting email")
        void shouldThrowExceptionForInvalidOtpWhenGettingEmail() {
            // Given
            String otp = "123456";
            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get(otp)).thenReturn(null);

            // When & Then
            AppException exception = assertThrows(AppException.class,
                    () -> otpService.getEmailByOtp(otp));
            assertEquals(ErrorCode.OTP_INVALID, exception.getErrorCode());
            verify(redisTemplate).opsForValue();
            verify(valueOperations).get(otp);
        }
    }

    @Nested
    @DisplayName("Check OTP Expiration Tests")
    class CheckOtpExpirationTests {

        @Test
        @DisplayName("Should return true for expired OTP")
        void shouldReturnTrueForExpiredOtp() {
            // Given
            String otp = "123456";
            when(redisTemplate.hasKey(otp)).thenReturn(false);

            // When
            boolean result = otpService.isOtpExpired(otp);

            // Then
            assertTrue(result);
            verify(redisTemplate).hasKey(otp);
        }

        @Test
        @DisplayName("Should return false for valid OTP")
        void shouldReturnFalseForValidOtp() {
            // Given
            String otp = "123456";
            when(redisTemplate.hasKey(otp)).thenReturn(true);

            // When
            boolean result = otpService.isOtpExpired(otp);

            // Then
            assertFalse(result);
            verify(redisTemplate).hasKey(otp);
        }

        @Test
        @DisplayName("Should return true when Redis returns null")
        void shouldReturnTrueWhenRedisReturnsNull() {
            // Given
            String otp = "123456";
            when(redisTemplate.hasKey(otp)).thenReturn(null);

            // When
            boolean result = otpService.isOtpExpired(otp);

            // Then
            assertTrue(result);
            verify(redisTemplate).hasKey(otp);
        }
    }

    @Nested
    @DisplayName("Remove OTP Tests")
    class RemoveOtpTests {

        @Test
        @DisplayName("Should successfully remove OTP")
        void shouldSuccessfullyRemoveOtp() {
            // Given
            String email = "test@example.com";
            when(redisTemplate.delete(email)).thenReturn(true);

            // When
            assertDoesNotThrow(() -> otpService.removeOtp(email));

            // Then
            verify(redisTemplate).delete(email);
        }
    }

    @Nested
    @DisplayName("Generate OTP Code Tests")
    class GenerateOtpCodeTests {

        @Test
        @DisplayName("Should generate valid 6-digit OTP")
        void shouldGenerateValid6DigitOtp() {
            // When
            String otp = otpService.generateOtpCode();

            // Then
            assertNotNull(otp);
            assertEquals(6, otp.length());
            assertTrue(otp.matches("\\d{6}"));
        }

        @Test
        @DisplayName("Should generate different OTPs on consecutive calls")
        void shouldGenerateDifferentOtpsOnConsecutiveCalls() {
            // When
            String otp1 = otpService.generateOtpCode();
            String otp2 = otpService.generateOtpCode();

            // Then
            assertNotNull(otp1);
            assertNotNull(otp2);
            assertNotEquals(otp1, otp2);
        }
    }
}