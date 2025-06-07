package org.cosmetic.com.controller;

import jakarta.validation.Valid;
import org.cosmetic.com.dto.request.LoginRequestDto;
import org.cosmetic.com.dto.response.ApiResponse;
import org.cosmetic.com.dto.response.LoginResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {




    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        return null;
    }
}