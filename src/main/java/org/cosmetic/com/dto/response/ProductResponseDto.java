package org.cosmetic.com.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class ProductResponseDto {
    private Long id;
    private String productName;
    private String description;
    private BigDecimal price;
    private Integer quantity;
    private String brandName;
    private List<String> categoryNames;
    private String imageUrl;
}