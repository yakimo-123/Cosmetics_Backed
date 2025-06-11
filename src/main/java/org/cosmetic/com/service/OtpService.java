package org.cosmetic.com.service;

public interface OtpService {
    public void saveOtp(String email, String otp);
    public String verifyOtp(String inputOtp);
    public String getEmailByOtp(String otp);
    public boolean isOtpExpired(String email);
    public void removeOtp(String email);
    public String generateOtpCode();
}
