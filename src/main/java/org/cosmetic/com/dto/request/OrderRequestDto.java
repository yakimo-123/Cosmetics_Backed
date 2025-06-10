package org.cosmetic.com.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.cosmetic.com.enums.OrderStatus;
import org.cosmetic.com.enums.PaymentMethod;

import java.util.List;

@Data
@Builder
public class OrderRequestDto {
    @NotEmpty(message = "Order details cannot be empty")
    @Size(min = 1, message = "Order must contain at least one item")
    @Valid
    private List<OrderDetailRequestDto> orderDetails;

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    private OrderStatus orderStatus = OrderStatus.PENDING;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;
}