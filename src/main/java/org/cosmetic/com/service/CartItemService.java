package org.cosmetic.com.service;

import org.cosmetic.com.model.CartItem;
import java.util.List;
import java.util.Optional;

public interface CartItemService {
    public void addItemToCart(Long cartId, Long productId, int quantity);
    public void removeItemFromCart(Long cartId, Long productId);
    public void updateItemQuantity(Long cartId, Long productId, int quantity);
    List<CartItem> findAll();
    Optional<CartItem> findById(Long id);
    CartItem save(CartItem cartItem);
    void deleteById(Long id);
}