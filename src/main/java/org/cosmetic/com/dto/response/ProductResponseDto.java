package org.cosmetic.com.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductResponseDto {
    private Long id;
    private String productName;
    private String description;
    private Double price;
    private Integer quantity;
    private String brandName;
}