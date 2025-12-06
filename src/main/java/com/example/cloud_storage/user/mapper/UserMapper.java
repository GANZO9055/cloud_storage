package com.example.cloud_storage.user.mapper;

import com.example.cloud_storage.user.dto.UserRequestDto;
import com.example.cloud_storage.user.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserRequestDto userRequestDto);
}
