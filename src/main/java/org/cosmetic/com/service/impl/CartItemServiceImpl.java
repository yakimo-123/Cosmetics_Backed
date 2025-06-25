package org.cosmetic.com.service.impl;

import lombok.RequiredArgsConstructor;
import org.cosmetic.com.exception.AppException;
import org.cosmetic.com.exception.ErrorCode;
import org.cosmetic.com.model.Cart;
import org.cosmetic.com.model.CartItem;
import org.cosmetic.com.model.Product;
import org.cosmetic.com.repository.CartItemRepository;
import org.cosmetic.com.repository.CartRepository;
import org.cosmetic.com.service.CartItemService;
import org.cosmetic.com.service.ProductService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {

    private final CartItemRepository cartItemRepository;
    private final ProductService productService;
    private final CartRepository cartRepository;

    @Override
    public void addItemToCart(Long cartId, Long productId, int quantity) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

        Product product = productService.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        // tạo CartItem “thô” (chỉ chứa product & quantity)
        CartItem item = new CartItem();
        item.setProduct(product);
        item.setQuantity(quantity);
        item.setUnitPrice(product.getPrice());

        cart.addCartItem(item);     // Cart lo xử lí gộp & subPrice

        cartRepository.save(cart);  // do CascadeType.ALL → CartItem tự lưu
    }

    @Override
    public void removeItemFromCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

        CartItem cartItem = getCartItem(cartId, productId);
        cart.removeCartItem(cartItem);
        cartRepository.save(cart);
    }

    @Override
    public void updateItemQuantity(Long cartId, Long productId, int quantity) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

        cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .ifPresentOrElse(cartItem -> {
                            cartItem.setQuantity(quantity);
                            cartItem.updateSubPrice();
                        },
                        () -> {
                            throw new AppException(ErrorCode.CART_ITEM_NOT_FOUND);
                        });
        // Update the cart total amount
        BigDecimal totalAmount = cart.getCartItems().stream()
                .map(CartItem::getSubPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalAmount(totalAmount);
        cartRepository.save(cart);
    }

    private CartItem getCartItem(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));
        return cart.getCartItems()
                .stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst().orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND));
    }
}