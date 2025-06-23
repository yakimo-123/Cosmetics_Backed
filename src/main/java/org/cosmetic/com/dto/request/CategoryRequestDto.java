package org.cosmetic.com.dto.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequestDto {
    private String categoryName;
    private String description;
    private String imageUrl;
    private Boolean status;
}