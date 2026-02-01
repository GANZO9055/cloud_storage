package com.example.cloud_storage.user.controller;

import com.example.cloud_storage.user.dto.UserResponseDto;
import com.example.cloud_storage.user.util.UserUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController {

    private UserUtil userUtil;

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getCurrentUser() {
        return new ResponseEntity<>(
                new UserResponseDto(userUtil.getUsername()),
                HttpStatus.OK
        );
    }
}
