package org.cosmetic.com.repository;

import org.cosmetic.com.enums.CartStatus;
import org.cosmetic.com.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserId(Long userId);

    Optional<Cart> findBySessionId(String sessionId);

    Optional<Cart> findByUserIdAndCartStatus(Long userId, CartStatus cartStatus);

    Optional<Cart> findBySessionIdAndCartStatus(String sessionId, CartStatus cartStatus);
}