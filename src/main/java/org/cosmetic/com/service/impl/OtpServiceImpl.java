package org.cosmetic.com.service.impl;

import lombok.AllArgsConstructor;
import org.cosmetic.com.service.OtpService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;


@Service
@AllArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final Duration OTP_EXPIRATION = Duration.ofMinutes(5);

    @Override
    public void saveOtp(String otp, String email) {

        redisTemplate.opsForValue().set(otp, email, OTP_EXPIRATION);
    }

    @Override
    public String verifyOtp(String inputOtp) {
        String email = redisTemplate.opsForValue().get(inputOtp);
        if (email != null) {
            // Optionally, remove the OTP after successful verification
            redisTemplate.delete(inputOtp);
            return email;
        }
        return null;
    }

    @Override
    public String getEmailByOtp(String otp) {
        return redisTemplate.opsForValue().get(otp);
    }

    @Override
    public boolean isOtpExpired(String email) {
        return false;
    }

    @Override
    public void removeOtp(String email) {
        redisTemplate.delete(email);
    }

    @Override
    public String generateOtpCode() {
        // Generate a random 6-digit OTP code
        return String.format("%06d", (int) (Math.random() * 1000000));
    }
}
