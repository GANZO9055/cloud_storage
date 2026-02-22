package com.example.cloud_storage.service.user;

import com.example.cloud_storage.dto.request.UserRequestDto;
import com.example.cloud_storage.dto.response.UserResponseDto;
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
