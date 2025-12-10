package com.example.cloud_storage.user.controller;

import com.example.cloud_storage.user.dto.UserRequestDto;
import com.example.cloud_storage.user.dto.UserResponseDto;
import com.example.cloud_storage.user.model.User;
import com.example.cloud_storage.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private UserService userService;

    @PostMapping("/sign-up")
    public ResponseEntity<UserResponseDto> registration(@Valid @RequestBody UserRequestDto userRequestDto,
                                                        HttpServletRequest request) {
        User user = userService.create(userRequestDto, request);
        return new ResponseEntity<>(
                new UserResponseDto(user.getUsername()),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/sign-in")
    public ResponseEntity<UserResponseDto> authorization(@Valid @RequestBody UserRequestDto userRequestDto,
                                                         HttpServletRequest request) {
        userService.authenticate(userRequestDto);
        UserDetails user = userService.loadUserByUsername(userRequestDto.getUsername());
        return new ResponseEntity<>(
                new UserResponseDto(user.getUsername()),
                HttpStatus.OK
        );
    }

    @PostMapping("/sign-out")
    public ResponseEntity<Void> logout(HttpSession session) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
