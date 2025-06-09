// src/main/java/org/cosmetic/com/repository/UserRepository.java
package org.cosmetic.com.repository;

import jakarta.validation.constraints.Email;
import org.cosmetic.com.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByUsername(String username);

    User findByEmail(@Email String email);
}