package com.example.cloud_storage.user.controller;

import com.example.cloud_storage.user.dto.UserResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {
    @GetMapping("/user/me")
    public ResponseEntity<UserResponseDto> getCurrentUser(Authentication authentication) {
        return new ResponseEntity<>(
                new UserResponseDto(authentication.getName()),
                HttpStatus.OK
        );
    }
}
