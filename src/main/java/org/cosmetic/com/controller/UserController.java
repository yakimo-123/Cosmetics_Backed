package org.cosmetic.com.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.cosmetic.com.dto.response.ApiResponse;
import org.cosmetic.com.dto.response.UserResponseDto;
import org.cosmetic.com.mapper.UserMapper;
import org.cosmetic.com.model.User;
import org.cosmetic.com.security.CustomUserDetails;
import org.cosmetic.com.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> getAllUsers() {
        List<User> users = userService.findAll();
        return ResponseEntity.ok(
                ApiResponse.<List<UserResponseDto>>builder()
                        .status(true)
                        .message("Fetched all users")
                        .data(users.stream().map(userMapper::toDto).toList())
                        .build()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserById(@PathVariable Long id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(ApiResponse.<UserResponseDto>builder()
                .status(true)
                .message("User found")
                .data(userMapper.toDto(user))
                .build());
    }

    @GetMapping("/my-profile")
    public ResponseEntity<ApiResponse<User>> getMyProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        String username = userDetails.getUsername();
        User user = userService.findByUsername(username);
        return ResponseEntity.ok(ApiResponse.<User>builder()
                .status(true)
                .message("User found")
                .data(user)
                .build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<User>> createUser(@RequestBody User user) {
        User saved = userService.save(user);
        return ResponseEntity.status(201).body(
                ApiResponse.<User>builder()
                        .status(true)
                        .message("User created")
                        .data(saved)
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> updateUser(@PathVariable Long id, @RequestBody User user) {
        return null;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(true)
                        .message("User deleted")
                        .data(null)
                        .build()
        );
    }
}