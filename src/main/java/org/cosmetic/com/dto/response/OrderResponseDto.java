package org.cosmetic.com.dto.response;

import lombok.Data;
import org.cosmetic.com.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponseDto {

    private Long id;
    private String username;
    private OrderStatus orderStatus;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private List<OrderDetailResponseDto> orderDetails;


}
