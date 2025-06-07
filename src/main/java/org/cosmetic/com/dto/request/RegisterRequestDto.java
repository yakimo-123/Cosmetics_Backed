package org.cosmetic.com.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegisterRequestDto {
    private String username;
    private String password;
    private String email;
    // Add more fields if needed (e.g., fullName, phone, etc.)
}