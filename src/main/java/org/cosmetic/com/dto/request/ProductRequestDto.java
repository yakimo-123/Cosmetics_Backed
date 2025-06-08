package org.cosmetic.com.dto.request;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProductRequestDto {
    private String name;
    private String description;
    private Double price;
    private Integer quantity;
    private List<Long> categoryIds;
}