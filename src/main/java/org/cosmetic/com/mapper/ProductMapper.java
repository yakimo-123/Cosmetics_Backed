package org.cosmetic.com.mapper;

import org.cosmetic.com.dto.request.ProductRequestDto;
import org.cosmetic.com.dto.response.ProductResponseDto;
import org.cosmetic.com.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductResponseDto toResponseDto(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "supplier", ignore = true)
    Product toEntity(ProductRequestDto dto);

    void updateEntityFromDto(ProductRequestDto dto, @MappingTarget Product product);
}