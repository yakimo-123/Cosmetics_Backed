package org.cosmetic.com.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CartRequestDto {
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Cart items are required")
    @Size(min = 1, message = "Cart must have at least one item")
    private List<CartItemRequestDto> cartItems;
}