
package org.cosmetic.com.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.util.List;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequestDto {
    @NotBlank(message = "Product name must not be blank")
    private String productName;

    private String description;

    @NotNull(message = "Price is required")
    private Double price;

    @NotNull(message = "Quantity is required")
    private Integer quantity;

    @NotNull(message = "Category IDs are required")
    @Size(min = 1, message = "At least one category ID is required")
    private List<Long> categoryIds;

    @NotNull(message = "Supplier ID is required")
    private Long supplierId;


}