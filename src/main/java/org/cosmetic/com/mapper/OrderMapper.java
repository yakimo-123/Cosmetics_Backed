package org.cosmetic.com.mapper;

import org.cosmetic.com.dto.request.OrderRequestDto;
import org.cosmetic.com.dto.response.OrderDetailResponseDto;
import org.cosmetic.com.dto.response.OrderResponseDto;
import org.cosmetic.com.model.Order;
import org.cosmetic.com.model.OrderDetail;
import org.mapstruct.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderStatus", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "orderDetails", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "paymentMethod", ignore = true)
    Order toEntity(OrderRequestDto requestDto);


    @Mapping(target = "orderDetails", source = "orderDetails")
    @Mapping(target = "createdAt", source = "orderDate")
    OrderResponseDto toDto(Order order);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.productName")
    OrderDetailResponseDto toDto(OrderDetail orderDetail);

    default LocalDateTime map(Instant instant) {
        return instant != null ? LocalDateTime.ofInstant(instant, ZoneId.systemDefault()) : null;
    }
    List<OrderDetailResponseDto> toDto(List<OrderDetail> orderDetails);

}