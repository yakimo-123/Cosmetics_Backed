package org.cosmetic.com.service;

import org.cosmetic.com.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> findAll();

    Optional<User> findById(Long id);

    User save(User user);

    void deleteById(Long id);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}