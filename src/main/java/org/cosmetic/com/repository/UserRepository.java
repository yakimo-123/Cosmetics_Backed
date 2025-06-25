// src/main/java/org/cosmetic/com/repository/UserRepository.java
package org.cosmetic.com.repository;

import jakarta.validation.constraints.Email;
import org.cosmetic.com.enums.Role;
import org.cosmetic.com.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(@Email String email);

    boolean existsByRole(Role role);
}