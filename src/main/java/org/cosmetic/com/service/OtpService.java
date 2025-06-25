package org.cosmetic.com.service;

public interface OtpService {
    void saveOtp(String keyOtp, String email);

    String verifyOtp(String inputOtp);

    String getEmailByOtp(String otp);

    boolean isOtpExpired(String email);

    void removeOtp(String email);

    String generateOtpCode();
}
