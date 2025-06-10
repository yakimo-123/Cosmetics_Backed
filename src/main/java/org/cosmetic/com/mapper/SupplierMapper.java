package org.cosmetic.com.mapper;


import org.cosmetic.com.dto.request.SupplierRequestDto;
import org.cosmetic.com.model.Supplier;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SupplierMapper {

    @Mapping(target = "supplierName", source = "name")
    public Supplier toEntity(SupplierRequestDto dto);
}
