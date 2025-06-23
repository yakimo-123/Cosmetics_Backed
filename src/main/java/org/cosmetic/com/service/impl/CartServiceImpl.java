package org.cosmetic.com.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.cosmetic.com.enums.CartStatus;
import org.cosmetic.com.exception.AppException;
import org.cosmetic.com.exception.ErrorCode;
import org.cosmetic.com.model.Cart;
import org.cosmetic.com.model.User;
import org.cosmetic.com.repository.CartRepository;
import org.cosmetic.com.repository.UserRepository;
import org.cosmetic.com.service.CartService;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    @Override
    public List<Cart> findAll() {
        return cartRepository.findAll();
    }

    @Override
    public Optional<Cart> findById(Long id) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));
        BigDecimal totalAmount = cart.getTotalAmount();
        cart.setTotalAmount(totalAmount);
        return cartRepository.findById(id);
    }

    @Transactional
    @Override
    public Cart getOrCreateCart(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Cart existingCart = cartRepository.findByUser_IdAndCartStatus(userId,CartStatus.ACTIVE).orElse(null);
        if (existingCart == null) {
            existingCart = Cart.builder()
                    .cartStatus(CartStatus.ACTIVE)
                    .user(user)
                    .build();
            return cartRepository.save(existingCart);
        }else {
            return existingCart;
        }
    }

    @Override
    public void clearCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        cart.getCartItems().clear();
        cartRepository.save(cart);
    }


    @Transactional
    @Override
    public void deleteById(Long id) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));
        cart.setCartStatus(CartStatus.DELETED);
        cartRepository.save(cart);
    }

    @Override
    public Optional<Cart> findByUserId(Long userId) {
        return cartRepository.findByUserId(userId);
    }

    @Override
    public Cart getActiveCart(Long userId) {
            return cartRepository.findByUserId(userId)
                    .orElseThrow(() -> new AppException(ErrorCode.ACTIVE_CART_NOT_FOUND));
    }
}