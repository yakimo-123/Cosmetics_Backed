package org.cosmetic.com.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BrandRequestDto {


    @NotBlank(message = "Name brand is required")
    @Size(max = 100, message = "Name brand must be at most 100 characters")
    private String name;

    @Size(max = 255, message = "Description must be at most 255 characters")
    private String description;

    private String imageUrl;
}