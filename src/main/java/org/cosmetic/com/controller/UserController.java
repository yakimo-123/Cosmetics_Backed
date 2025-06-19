package org.cosmetic.com.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.cosmetic.com.dto.response.ApiResponse;
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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        List<User> users = userService.findAll();
        return ResponseEntity.ok(
                ApiResponse.<List<User>>builder()
                        .status(true)
                        .message("Fetched all users")
                        .data(users)
                        .build()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(user -> ResponseEntity.ok(
                        ApiResponse.<User>builder()
                                .status(true)
                                .message("User found")
                                .data(user)
                                .build()
                ))
                .orElse(ResponseEntity.status(404).body(
                        ApiResponse.<User>builder()
                                .status(false)
                                .message("User not found")
                                .data(null)
                                .build()
                ));
    }

    @GetMapping("/my-profile")
    public ResponseEntity<ApiResponse<User>> getMyProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        String username = userDetails.getUsername();
        User user = userService.findByUsername(username).orElseThrow(
                () -> new IllegalArgumentException("User not found"
        ));
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
        return userService.findById(id)
                .map(existing -> {
                    user.setId(id);
                    User updated = userService.save(user);
                    return ResponseEntity.ok(
                            ApiResponse.<User>builder()
                                    .status(true)
                                    .message("User updated")
                                    .data(updated)
                                    .build()
                    );
                })
                .orElse(ResponseEntity.status(404).body(
                        ApiResponse.<User>builder()
                                .status(false)
                                .message("User not found")
                                .data(null)
                                .build()
                ));
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