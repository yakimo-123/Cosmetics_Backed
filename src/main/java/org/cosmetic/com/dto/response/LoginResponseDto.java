package org.cosmetic.com.dto.response;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginResponseDto {
    private String accessToken;
    private String username;

}
