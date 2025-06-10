package org.cosmetic.com.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CartItemResponseDto {
    private Long id;
    private Long productId;
    private int quantity;
    private BigDecimal subPrice;
}