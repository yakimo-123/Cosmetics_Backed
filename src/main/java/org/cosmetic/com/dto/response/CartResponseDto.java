package org.cosmetic.com.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class CartResponseDto {
    private Long id;
    private Long userId;
    private List<CartItemResponseDto> cartItems;
    private BigDecimal totalAmount;
}