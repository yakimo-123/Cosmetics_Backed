package org.cosmetic.com.mapper;

import org.cosmetic.com.dto.request.ProductRequestDto;
import org.cosmetic.com.dto.response.ProductResponseDto;
import org.cosmetic.com.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductResponseDto toResponseDto(Product product);
    Product toEntity(ProductRequestDto dto);
    void updateEntityFromDto(ProductRequestDto dto, @MappingTarget Product product);
}