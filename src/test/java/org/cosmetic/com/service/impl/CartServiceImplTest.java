package org.cosmetic.com.service.impl;

import org.cosmetic.com.enums.CartStatus;
import org.cosmetic.com.exception.AppException;
import org.cosmetic.com.model.Cart;
import org.cosmetic.com.model.CartItem;
import org.cosmetic.com.model.User;
import org.cosmetic.com.repository.CartRepository;
import org.cosmetic.com.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @Mock
    private CartRepository cartRepository;

    @Mock
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
        @DisplayName("Should create a new Cart if none exists")
        void shouldCreateNewCartIfNoneExists() {
            // Given
            Long userId = 1L;
            User user = new User();
            user.setId(userId);  // Giả sử userId là 1L

            // Giả lập rằng không có Cart ACTIVE cho user
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(cartRepository.findByUser_IdAndCartStatus(userId, CartStatus.ACTIVE)).thenReturn(Optional.empty());

            // Giả lập lưu Cart mới
            Cart newCart = new Cart();
            newCart.setCartStatus(CartStatus.ACTIVE);
            newCart.setUser(user);
            when(cartRepository.save(any(Cart.class))).thenReturn(newCart);

            // When
            Cart result = cartService.getOrCreateCart(userId);

            // Then
            assertNotNull(result);
            assertEquals(CartStatus.ACTIVE, result.getCartStatus());
            assertEquals(userId, result.getUser().getId());
            verify(cartRepository).save(any(Cart.class)); // Kiểm tra xem cartRepository.save đã được gọi
        }

        @Test
        @DisplayName("Should return existing Cart if it already exists")
        void shouldReturnExistingCartIfItExists() {
            // Given
            Long userId = 1L;
            User user = new User();
            user.setId(userId);

            // Giả lập rằng Cart ACTIVE đã tồn tại cho user
            Cart existingCart = new Cart();
            existingCart.setCartStatus(CartStatus.ACTIVE);
            existingCart.setUser(user);

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(cartRepository.findByUser_IdAndCartStatus(userId, CartStatus.ACTIVE)).thenReturn(Optional.of(existingCart));

            // When
            Cart result = cartService.getOrCreateCart(userId);

            // Then
            assertNotNull(result);
            assertEquals(existingCart, result);  // Kiểm tra xem Cart trả về có giống với Cart đã tồn tại không
            verify(cartRepository, never()).save(any(Cart.class)); // Kiểm tra rằng phương thức save không được gọi
        }


    }

    @Nested
    @DisplayName("Clear Cart Tests")
    class ClearCartTests {

        @Test
        @DisplayName("Should clear existing cart items")
        void shouldClearExistingCartItems() {
            // Given: Tạo một cart với một item trong giỏ hàng
            Long userId = 1L;
            Cart cart = new Cart();
            cart.setCartItems(new ArrayList<>());
            cart.getCartItems().add(new CartItem());  // Thêm một item vào giỏ hàng

            // Giả lập trả về giỏ hàng có trạng thái ACTIVE từ repository
            when(cartRepository.findByUser_IdAndCartStatus(userId, CartStatus.ACTIVE))
                    .thenReturn(Optional.of(cart));

            // Giả lập phương thức save để kiểm tra sau khi xóa item
            when(cartRepository.save(any(Cart.class))).thenReturn(cart);

            // When: Gọi phương thức clearCart để xóa các item trong giỏ hàng
            cartService.clearCart(userId);

            // Then: Kiểm tra xem cartItems có bị xóa không
            assertTrue(cart.getCartItems().isEmpty(), "Cart items should be cleared");
            verify(cartRepository).save(cart);  // Kiểm tra xem phương thức save đã được gọi
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