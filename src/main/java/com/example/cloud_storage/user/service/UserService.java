package com.example.cloud_storage.user.service;

import com.example.cloud_storage.user.dto.UserRequestDto;
import com.example.cloud_storage.user.dto.UserResponseDto;
import com.example.cloud_storage.user.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {
    UserResponseDto create(UserRequestDto userRequestDto,
                           HttpServletRequest request,
                           HttpServletResponse response);
    UserResponseDto authenticate(UserRequestDto userRequestDto,
                      HttpServletRequest request,
                      HttpServletResponse response);
}
