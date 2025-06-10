package org.cosmetic.com.dto.request;

import lombok.Data;

@Data
public class EmailVerificationRequest {
    private String email;
    private String otp;
}
