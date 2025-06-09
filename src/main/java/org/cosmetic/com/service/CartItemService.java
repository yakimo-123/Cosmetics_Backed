package org.cosmetic.com.service;

import org.cosmetic.com.model.CartItem;
import java.util.List;
import java.util.Optional;

public interface CartItemService {
    List<CartItem> findAll();
    Optional<CartItem> findById(Long id);
    CartItem save(CartItem cartItem);
    void deleteById(Long id);
}