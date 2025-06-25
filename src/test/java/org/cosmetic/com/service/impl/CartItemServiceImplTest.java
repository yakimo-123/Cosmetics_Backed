package org.cosmetic.com.service.impl;

import org.cosmetic.com.exception.AppException;
import org.cosmetic.com.exception.ErrorCode;
import org.cosmetic.com.model.Cart;
import org.cosmetic.com.model.CartItem;
import org.cosmetic.com.model.Product;
import org.cosmetic.com.repository.CartItemRepository;
import org.cosmetic.com.repository.CartRepository;
import org.cosmetic.com.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartItemServiceImplTest {

    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private ProductService productService;
    @Mock
    private CartRepository cartRepository;

    private CartItemServiceImpl cartItemService;

    @BeforeEach
    void setUp() {
        cartItemService = new CartItemServiceImpl(cartItemRepository, productService, cartRepository);
    }

    @Nested
    @DisplayName("Add Item To Cart Tests")
    class AddItemToCartTests {

        @Test
        @DisplayName("Should successfully add new item to cart")
        void shouldAddNewItemToCart() {
            // Given
            Long cartId = 1L;
            Long productId = 1L;
            int quantity = 2;

            Cart cart = new Cart();
            cart.setId(cartId);
            cart.setCartItems(new ArrayList<>());

            Product product = new Product();
            product.setId(productId);
            product.setPrice(BigDecimal.valueOf(10.00));

            when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
            when(productService.findById(productId)).thenReturn(Optional.of(product));

            // When
            cartItemService.addItemToCart(cartId, productId, quantity);

            // Then
            verify(cartRepository).save(cart);
            assertEquals(1, cart.getCartItems().size());

            CartItem savedItem = cart.getCartItems().get(0);
            assertEquals(quantity, savedItem.getQuantity());
            assertEquals(product.getPrice(), savedItem.getUnitPrice());
            assertEquals(product.getPrice().multiply(BigDecimal.valueOf(quantity)), savedItem.getSubPrice());
        }

        @Test
        @DisplayName("Should update quantity when adding existing item to cart")
        void shouldUpdateQuantityForExistingItem() {
            // Given
            Long cartId = 1L;
            Long productId = 1L;
            int initialQuantity = 2;
            int additionalQuantity = 3;

            Cart cart = new Cart();
            cart.setId(cartId);
            cart.setCartItems(new ArrayList<>());

            Product product = new Product();
            product.setId(productId);
            product.setPrice(BigDecimal.valueOf(10.00));

            CartItem existingItem = new CartItem();
            existingItem.setId(1L);
            existingItem.setProduct(product);
            existingItem.setQuantity(initialQuantity);
            existingItem.setUnitPrice(product.getPrice());
            cart.addCartItem(existingItem);

            when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
            when(productService.findById(productId)).thenReturn(Optional.of(product));

            // When
            cartItemService.addItemToCart(cartId, productId, additionalQuantity);

            // Then
            assertEquals(initialQuantity + additionalQuantity, existingItem.getQuantity());
            assertEquals(1, cart.getCartItems().size());
        }

        @Test
        @DisplayName("Should throw exception when cart not found")
        void shouldThrowExceptionWhenCartNotFound() {
            // Given
            Long cartId = 1L;
            when(cartRepository.findById(cartId)).thenReturn(Optional.empty());

            // When & Then
            AppException exception = assertThrows(AppException.class,
                    () -> cartItemService.addItemToCart(cartId, 1L, 1));
            assertEquals(ErrorCode.CART_NOT_FOUND, exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("Remove Item From Cart Tests")
    class RemoveItemFromCartTests {

        @Test
        @DisplayName("Should successfully remove item from cart")
        void shouldRemoveItemFromCart() {
            // Given
            Long cartId = 1L;
            Long productId = 1L;

            Product product = new Product();
            product.setId(productId);

            CartItem cartItem = new CartItem();
            cartItem.setProduct(product);

            Cart cart = new Cart();
            cart.setId(cartId);
            cart.setCartItems(new ArrayList<>());
            cart.addCartItem(cartItem);

            when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));

            // When
            cartItemService.removeItemFromCart(cartId, productId);

            // Then
            verify(cartRepository).save(cart);
            assertTrue(cart.getCartItems().isEmpty());
        }

        @Test
        @DisplayName("Should throw exception when trying to remove non-existent item")
        void shouldThrowExceptionWhenItemNotFound() {
            // Given
            Long cartId = 1L;
            Long productId = 1L;

            Cart cart = new Cart();
            cart.setId(cartId);
            cart.setCartItems(new ArrayList<>());

            when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));

            // When & Then
            AppException exception = assertThrows(AppException.class,
                    () -> cartItemService.removeItemFromCart(cartId, productId));
            assertEquals(ErrorCode.CART_ITEM_NOT_FOUND, exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("Update Item Quantity Tests")
    class UpdateItemQuantityTests {

        @Test
        @DisplayName("Should successfully update item quantity")
        void shouldUpdateItemQuantity() {
            // Given
            Long cartId = 1L;
            Long productId = 1L;
            int newQuantity = 5;

            Product product = new Product();
            product.setId(productId);
            product.setPrice(BigDecimal.valueOf(10.00));

            CartItem cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setQuantity(2);
            cartItem.setUnitPrice(product.getPrice());

            Cart cart = new Cart();
            cart.setId(cartId);
            cart.setCartItems(new ArrayList<>());
            cart.addCartItem(cartItem);

            when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));

            // When
            cartItemService.updateItemQuantity(cartId, productId, newQuantity);

            // Then
            verify(cartRepository).save(cart);
            assertEquals(newQuantity, cartItem.getQuantity());
            assertEquals(BigDecimal.valueOf(50.00), cart.getTotalAmount());
        }

        @Test
        @DisplayName("Should throw exception when updating quantity of non-existent item")
        void shouldThrowExceptionWhenUpdatingNonExistentItem() {
            // Given
            Long cartId = 1L;
            Long productId = 1L;
            Cart cart = new Cart();
            cart.setId(cartId);
            cart.setCartItems(new ArrayList<>());

            when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));

            // When & Then
            AppException exception = assertThrows(AppException.class,
                    () -> cartItemService.updateItemQuantity(cartId, productId, 5));
            assertEquals(ErrorCode.CART_ITEM_NOT_FOUND, exception.getErrorCode());
        }
    }
}