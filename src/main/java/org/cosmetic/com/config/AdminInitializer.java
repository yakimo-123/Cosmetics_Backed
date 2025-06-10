package org.cosmetic.com.config;

import lombok.extern.slf4j.Slf4j;
import org.cosmetic.com.enums.Role;
import org.cosmetic.com.model.User;
import org.cosmetic.com.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.default.email}")
    private String adminDefaultEmail;

    @Value("${admin.default.password}")
    private String adminPassword;

    public AdminInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        boolean adminExists = userRepository.existsByRole(Role.ADMIN);
        if (!adminExists) {
            User admin = User.builder()
                    .email(adminDefaultEmail)
                    .username(UUID.randomUUID().toString())
                    .password(passwordEncoder.encode(adminPassword))
                    .role(Role.ADMIN)
                    .enabled(true)
                    .build();
            userRepository.save(admin);
            log.info("Admin added");
        } else {
            log.info("Admin already exists, skipping creation.");
        }
    }
}
