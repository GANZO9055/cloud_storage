package com.example.cloud_storage.user.service;

import com.example.cloud_storage.user.dto.UserRequestDto;
import com.example.cloud_storage.user.model.User;

public interface UserService {
    User create(UserRequestDto userRequestDto);
    User getUser(UserRequestDto userRequestDto);
}
