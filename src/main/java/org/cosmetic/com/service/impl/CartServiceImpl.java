package org.cosmetic.com.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.cosmetic.com.enums.CartStatus;
import org.cosmetic.com.model.Cart;
import org.cosmetic.com.model.User;
import org.cosmetic.com.repository.CartRepository;
import org.cosmetic.com.service.CartService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    @Override
    public List<Cart> findAll() {
        return cartRepository.findAll();
    }

    @Override
    public Optional<Cart> findById(Long id) {
        Cart cart = cartRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Cart not found with id: " + id)
        );
        BigDecimal totalAmount = cart.getTotalAmount();
        cart.setTotalAmount(totalAmount);
        return cartRepository.findById(id);
    }

    @Override
    public Cart getOrCreateCart(Long userId, String sessionId) {
        Optional<Cart> existingCart;
        if(userId != null) {
            existingCart = cartRepository.findByUserIdAndCartStatus(userId,CartStatus.ACTIVE);
        } else {
            existingCart = cartRepository.findBySessionIdAndCartStatus(sessionId,CartStatus.ACTIVE);
        }
        if (existingCart.isPresent()) {
            return existingCart.get();
        }

        Cart newCart = new Cart();
        if (userId != null) {
            User user = new User();
            user.setId(userId);
            newCart.setUser(user);
        }else {
            newCart.setSessionId(sessionId);
        }
        newCart.setCartStatus(CartStatus.ACTIVE);

        return cartRepository.save(newCart);
    }

    @Override
    public void clearCart(Long userId, String sessionId) {
        Cart cart = getOrCreateCart(userId,sessionId);
        cart.getCartItems().clear();
        cartRepository.save(cart);
    }


    @Transactional
    @Override
    public void deleteById(Long id) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found with id: " + id));
        cart.setCartStatus(CartStatus.DELETED);
        cartRepository.save(cart);
    }

    @Override
    public Optional<Cart> findByUserId(Long userId) {
        return cartRepository.findByUserId(userId);
    }

    private Cart getActiveCart(Long userId, String sessionId) {
        if (userId != null) {
            return cartRepository.findByUserId(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Active cart not found: " + userId));
        } else {
            return cartRepository.findBySessionId(sessionId)
                    .orElseThrow(() -> new IllegalArgumentException("Active cart not found: " + sessionId));
        }
    }

}