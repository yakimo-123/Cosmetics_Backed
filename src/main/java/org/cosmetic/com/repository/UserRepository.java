// src/main/java/org/cosmetic/com/repository/UserRepository.java
package org.cosmetic.com.repository;

import org.cosmetic.com.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}