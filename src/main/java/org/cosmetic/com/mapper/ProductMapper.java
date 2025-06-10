package org.cosmetic.com.mapper;

import org.cosmetic.com.dto.request.ProductRequestDto;
import org.cosmetic.com.dto.response.ProductResponseDto;
import org.cosmetic.com.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "brand.name",target = "brandName" )
    @Mapping(source = "categories", target = "categoryNames",qualifiedByName = "categoryNames")
    @Mapping(source = "inventory.quantity", target = "quantity")
    ProductResponseDto toResponseDto(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "supplier", ignore = true)
    Product toEntity(ProductRequestDto dto);

    @Named("categoryNames")
    static List<String> mapCategoryNames(List<org.cosmetic.com.model.Category> categories) {
        if (categories == null) return null;
        return categories.stream()
                .map(org.cosmetic.com.model.Category::getCategoryName)
                .collect(Collectors.toList());
    }

    void updateEntityFromDto(ProductRequestDto dto, @MappingTarget Product product);
}