package com.example.cloud_storage.user.service;

import com.example.cloud_storage.user.dto.UserRequestDto;
import com.example.cloud_storage.user.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {
    User create(UserRequestDto userRequestDto,
                HttpServletRequest request,
                HttpServletResponse response);
    User authenticate(UserRequestDto userRequestDto,
                      HttpServletRequest request,
                      HttpServletResponse response);
}
