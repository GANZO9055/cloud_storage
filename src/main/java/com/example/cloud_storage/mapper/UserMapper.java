package com.example.cloud_storage.mapper;

import com.example.cloud_storage.dto.response.UserResponseDto;
import com.example.cloud_storage.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "role", constant = "USER")
    User toUser(String username, String password);

    UserResponseDto toUserResponse(String username);
}
