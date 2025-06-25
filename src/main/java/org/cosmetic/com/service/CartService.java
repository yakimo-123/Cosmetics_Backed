package org.cosmetic.com.service;

import org.cosmetic.com.model.Cart;

import java.util.List;
import java.util.Optional;

public interface CartService {
    List<Cart> findAll();

    Optional<Cart> findById(Long id);

    Cart getOrCreateCart(Long userId);

    void clearCart(Long userId);

    void deleteById(Long id);

    Optional<Cart> findByUserId(Long userId);

    Cart getActiveCart(Long userId);
}