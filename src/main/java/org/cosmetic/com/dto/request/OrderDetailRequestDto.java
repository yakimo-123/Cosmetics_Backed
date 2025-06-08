package org.cosmetic.com.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;



@Data
public  class OrderDetailRequestDto {
    @NotNull
    private Long productId;

    @NotNull
    private Integer quantity;

}