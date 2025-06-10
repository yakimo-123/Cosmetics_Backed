package org.cosmetic.com.mapper;

import org.cosmetic.com.dto.request.CartItemRequestDto;
import org.cosmetic.com.dto.response.CartItemResponseDto;
import org.cosmetic.com.model.CartItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartItemMapper {
    CartItem toEntity(CartItemRequestDto dto);

    CartItemResponseDto toResponseDto(CartItem cartItem);
}