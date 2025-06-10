package org.cosmetic.com.controller;

import lombok.RequiredArgsConstructor;
import org.cosmetic.com.dto.response.ApiResponse;
import org.cosmetic.com.model.Cart;
import org.cosmetic.com.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Cart>>> getAllCarts() {
        List<Cart> carts = cartService.findAll();
        return ResponseEntity.ok(ApiResponse.<List<Cart>>builder()
                .status(true)
                .message("Carts fetched successfully")
                .data(carts)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Cart>> getCartById(@PathVariable Long id) {
        return cartService.findById(id)
                .map(cart -> ResponseEntity.ok(ApiResponse.<Cart>builder()
                        .status(true)
                        .message("Cart found")
                        .data(cart)
                        .build()))
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Cart>> createCart(@RequestBody Cart cart) {
        Cart savedCart = cartService.save(cart);
        return ResponseEntity.status(201).body(ApiResponse.<Cart>builder()
                .status(true)
                .message("Cart created successfully")
                .data(savedCart)
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Cart>> updateCart(@PathVariable Long id, @RequestBody Cart cart) {
        return cartService.findById(id)
                .map(existingCart -> {
                    cart.setId(id);
                    Cart updated = cartService.save(cart);
                    return ResponseEntity.ok(ApiResponse.<Cart>builder()
                            .status(true)
                            .message("Cart updated successfully")
                            .data(updated)
                            .build());
                }).orElseThrow(() -> new IllegalArgumentException("Cart not found"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCart(@PathVariable Long id) {
        cartService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(true)
                .message("Cart deleted successfully")
                .data(null)
                .build());
    }
}