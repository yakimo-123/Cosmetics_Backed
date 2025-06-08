package org.cosmetic.com.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderDetailResponseDto {
    private Long id;
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subPrice;
}