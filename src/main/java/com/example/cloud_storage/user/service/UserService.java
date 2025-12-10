package com.example.cloud_storage.user.service;

import com.example.cloud_storage.user.dto.UserRequestDto;
import com.example.cloud_storage.user.model.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User create(UserRequestDto userRequestDto, HttpServletRequest request);
    User authenticate(UserRequestDto userRequestDto);
}
