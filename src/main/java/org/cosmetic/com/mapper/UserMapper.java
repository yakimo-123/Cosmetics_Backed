package org.cosmetic.com.mapper;


import org.cosmetic.com.dto.response.UserResponseDto;
import org.cosmetic.com.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDto toDto(User user);

    User toEntity(UserResponseDto dto);
}