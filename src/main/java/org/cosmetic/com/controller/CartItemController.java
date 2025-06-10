package org.cosmetic.com.controller;

import lombok.RequiredArgsConstructor;
import org.cosmetic.com.dto.response.ApiResponse;
import org.cosmetic.com.model.CartItem;
import org.cosmetic.com.service.CartItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/cart-items")
@RequiredArgsConstructor
public class CartItemController {

    private final CartItemService cartItemService;


    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Void>> addItemToCart(
            @RequestParam(required = false) Long cartId,
            @RequestParam Long productId,
            @RequestParam int quantity) {
        cartItemService.addItemToCart(cartId, productId, quantity);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(true)
                        .message("Item added to cart")
                        .data(null)
                        .build()
        );
    }

    @DeleteMapping("/remove")
    public ResponseEntity<ApiResponse<Void>> removeItemFromCart(@RequestParam Long cartId, @RequestParam Long productId) {
        cartItemService.removeItemFromCart(cartId, productId);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(true)
                        .message("Item removed from cart")
                        .data(null)
                        .build()
        );
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<Void>> updateItemQuantity(
            @RequestParam Long cartId,
            @RequestParam Long productId,
            @RequestParam int quantity) {
        cartItemService.updateItemQuantity(cartId, productId, quantity);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(true)
                        .message("Item quantity updated")
                        .data(null)
                        .build()
        );
    }
}