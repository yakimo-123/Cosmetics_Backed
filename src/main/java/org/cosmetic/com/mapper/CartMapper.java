package org.cosmetic.com.mapper;

import org.cosmetic.com.dto.request.CartRequestDto;
import org.cosmetic.com.dto.response.CartResponseDto;
import org.cosmetic.com.model.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {
    Cart toEntity(CartRequestDto dto);

    @Mapping(target = "totalAmount", source = "totalAmount")
    CartResponseDto toResponseDto(Cart cart);
}