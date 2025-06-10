package org.cosmetic.com.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductResponseDto {
    private Long id;
    private String productName;
    private String description;
    private BigDecimal price;
    private Integer quantity;
    private String brandName;
    private String categoryName;
    private String imageUrl;
}