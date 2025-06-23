package org.cosmetic.com.mapper;

import org.cosmetic.com.dto.request.OrderDetailRequestDto;
import org.cosmetic.com.dto.response.OrderDetailResponseDto;
import org.cosmetic.com.model.OrderDetail;
import org.cosmetic.com.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderDetailMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "product", source = "product")
    @Mapping(target = "unitPrice", source = "product.price")
    @Mapping(target = "subPrice", ignore = true)
    OrderDetail toEntity(OrderDetailRequestDto requestDto, Product product);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.productName")
    OrderDetailResponseDto toDto(OrderDetail orderDetail);

    List<OrderDetailResponseDto> toDtoList(List<OrderDetail> orderDetails);


    default String getProductName(Product product) {
        return product != null ? product.getProductName() : "";
    }

    default Long getProductId(Product product) {
        return product != null ? product.getId() : null;
    }

}