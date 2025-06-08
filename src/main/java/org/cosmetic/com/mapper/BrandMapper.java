package org.cosmetic.com.mapper;

import org.cosmetic.com.dto.request.BrandRequestDto;
import org.cosmetic.com.dto.response.BrandResponseDto;
import org.cosmetic.com.model.Brand;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface BrandMapper {
    Brand toEntity(BrandRequestDto dto);

    BrandResponseDto toResponseDto(Brand brand);
}