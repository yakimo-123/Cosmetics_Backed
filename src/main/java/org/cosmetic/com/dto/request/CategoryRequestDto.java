package org.cosmetic.com.dto.request;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryRequestDto {
    private String categoryName;
    private String description;
    private String imageUrl;
    private Boolean status;
}