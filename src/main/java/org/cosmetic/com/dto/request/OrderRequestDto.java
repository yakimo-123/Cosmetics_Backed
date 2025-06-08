package org.cosmetic.com.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequestDto {
    @NotEmpty
    private List<OrderDetailRequestDto> orderDetails;


}