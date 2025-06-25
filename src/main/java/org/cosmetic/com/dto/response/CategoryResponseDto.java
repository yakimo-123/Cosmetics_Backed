package org.cosmetic.com.dto.response;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
public class CategoryResponseDto implements Serializable {
    private Long id;
    private String categoryName;
    private String description;
    private String imageUrl;
    private Boolean status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}