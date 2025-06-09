package org.cosmetic.com.dto.response;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class LoginResponseDto {
    private String accessToken;
    private String username;
}
