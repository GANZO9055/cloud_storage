package com.example.cloud_storage.user.mapper;

import com.example.cloud_storage.user.dto.UserResponseDto;
import com.example.cloud_storage.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "type", constant = "USER")
    User toUser(String username, String password);

    UserResponseDto toUserResponse(String username);
}
