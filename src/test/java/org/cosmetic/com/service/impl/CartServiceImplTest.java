package org.cosmetic.com.service.impl;

import org.cosmetic.com.enums.CartStatus;
import org.cosmetic.com.exception.AppException;
import org.cosmetic.com.model.Cart;
import org.cosmetic.com.model.CartItem;
import org.cosmetic.com.repository.CartRepository;
import org.cosmetic.com.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @MockitoBean
    private CartRepository cartRepository;

    @MockitoBean
    private UserRepository userRepository;

    private CartServiceImpl cartService;

    @BeforeEach
    void setUp() {
        cartService = new CartServiceImpl(cartRepository, userRepository);
    }

    @Nested
    @DisplayName("Find All Carts Tests")
    class FindAllTests {
        
        @Test
        @DisplayName("Should return all carts")
        void shouldReturnAllCarts() {
            // Given
            List<Cart> carts = Arrays.asList(new Cart(), new Cart());
            when(cartRepository.findAll()).thenReturn(carts);

            // When
            List<Cart> result = cartService.findAll();

            // Then
            assertEquals(2, result.size());
            verify(cartRepository).findAll();
        }
    }

    @Nested
    @DisplayName("Find By Id Tests")
    class FindByIdTests {

        @Test
        @DisplayName("Should return cart when found")
        void shouldReturnCartWhenFound() {
            // Given
            Long cartId = 1L;
            Cart cart = new Cart();
            cart.setId(cartId);
            cart.setTotalAmount(BigDecimal.TEN);

            when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));

            // When
            Optional<Cart> result = cartService.findById(cartId);

            // Then
            assertTrue(result.isPresent());
            assertEquals(cartId, result.get().getId());
            assertEquals(BigDecimal.TEN, result.get().getTotalAmount());
        }

        @Test
        @DisplayName("Should throw exception when cart not found")
        void shouldThrowExceptionWhenCartNotFound() {
            // Given
            Long cartId = 1L;
            when(cartRepository.findById(cartId)).thenReturn(Optional.empty());

            // When & Then
            assertThrows(AppException.class, () -> cartService.findById(cartId));
        }
    }

    @Nested
    @DisplayName("Get Or Create Cart Tests")
    class GetOrCreateCartTests {

        @Test
        @DisplayName("Should return existing cart for user")
        void shouldReturnExistingCartForUser() {
            // Given
            Long userId = 1L;
            Cart existingCart = new Cart();
            existingCart.setCartStatus(CartStatus.ACTIVE);

            when(cartRepository.findByUser_IdAndCartStatus(userId, CartStatus.ACTIVE))
                    .thenReturn(Optional.of(existingCart));

            // When
            Cart result = cartService.getOrCreateCart(userId);

            // Then
            assertNotNull(result);
            verify(cartRepository, never()).save(any(Cart.class));
        }

        @Test
        @DisplayName("Should create new cart for user when not exists")
        void shouldCreateNewCartForUser() {
            // Given
            Long userId = 1L;
            when(cartRepository.findByUser_IdAndCartStatus(userId, CartStatus.ACTIVE))
                    .thenReturn(Optional.empty());
            when(cartRepository.save(any(Cart.class))).thenAnswer(i -> i.getArguments()[0]);

            // When
            Cart result = cartService.getOrCreateCart(userId);

            // Then
            assertNotNull(result);
            assertEquals(CartStatus.ACTIVE, result.getCartStatus());
            assertNotNull(result.getUser());
            assertEquals(userId, result.getUser().getId());
            verify(cartRepository).save(any(Cart.class));
        }


    }

    @Nested
    @DisplayName("Clear Cart Tests")
    class ClearCartTests {

        @Test
        @DisplayName("Should clear existing cart items")
        void shouldClearExistingCartItems() {
            // Given
            Long userId = 1L;
            Cart cart = new Cart();
            cart.setCartItems(new ArrayList<>());
            cart.getCartItems().add(new CartItem());

            when(cartRepository.findByUser_IdAndCartStatus(userId, CartStatus.ACTIVE))
                    .thenReturn(Optional.of(cart));
            when(cartRepository.save(any(Cart.class))).thenReturn(cart);

            // When
            cartService.clearCart(userId);

            // Then
            assertTrue(cart.getCartItems().isEmpty());
            verify(cartRepository).save(cart);
        }
    }

    @Nested
    @DisplayName("Delete Cart Tests")
    class DeleteCartTests {

        @Test
        @DisplayName("Should mark cart as deleted")
        void shouldMarkCartAsDeleted() {
            // Given
            Long cartId = 1L;
            Cart cart = new Cart();
            cart.setId(cartId);
            cart.setCartStatus(CartStatus.ACTIVE);

            when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
            when(cartRepository.save(any(Cart.class))).thenReturn(cart);

            // When
            cartService.deleteById(cartId);

            // Then
            assertEquals(CartStatus.DELETED, cart.getCartStatus());
            verify(cartRepository).save(cart);
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existent cart")
        void shouldThrowExceptionWhenDeletingNonExistentCart() {
            // Given
            Long cartId = 1L;
            when(cartRepository.findById(cartId)).thenReturn(Optional.empty());

            // When & Then
            assertThrows(AppException.class, () -> cartService.deleteById(cartId));
        }
    }

    @Nested
    @DisplayName("Get Active Cart Tests")
    class GetActiveCartTests {

        @Test
        @DisplayName("Should return active cart for user")
        void shouldReturnActiveCartForUser() {
            // Given
            Long userId = 1L;
            Cart activeCart = new Cart();
            when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(activeCart));

            // When
            Cart result = cartService.getActiveCart(userId);

            // Then
            assertNotNull(result);
            verify(cartRepository).findByUserId(userId);
        }

        @Test
        @DisplayName("Should throw exception when no active cart found for user")
        void shouldThrowExceptionWhenNoActiveCartFoundForUser() {
            // Given
            Long userId = 1L;
            when(cartRepository.findByUserId(userId)).thenReturn(Optional.empty());

            // When & Then
            assertThrows(AppException.class, () -> cartService.getActiveCart(userId));
        }
    }
}