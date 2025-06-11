package org.cosmetic.com.mapper;

import org.cosmetic.com.dto.request.ReviewRequestDto;
import org.cosmetic.com.model.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "reviewStatus", ignore = true)
    @Mapping(target = "reply", ignore = true)
    Review toEntity(ReviewRequestDto dto);
}