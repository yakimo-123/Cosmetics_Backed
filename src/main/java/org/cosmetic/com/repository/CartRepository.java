package org.cosmetic.com.repository;

import org.cosmetic.com.enums.CartStatus;
import org.cosmetic.com.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserId(Long userId);

    Optional<Cart> findByUser_IdAndCartStatus(Long userId, CartStatus cartStatus);

    boolean existsByIdAndCartStatus(Long id, CartStatus cartStatus);

}