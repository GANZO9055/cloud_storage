package com.example.cloud_storage.user.controller;

import com.example.cloud_storage.user.dto.UserRequestDto;
import com.example.cloud_storage.user.dto.UserResponseDto;
import com.example.cloud_storage.user.model.User;
import com.example.cloud_storage.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class AuthController {

    private UserService userService;

    @PostMapping("/auth/sign-up")
    public ResponseEntity<UserResponseDto> registration(@Valid @RequestBody UserRequestDto userRequestDto,
                                                        HttpSession session) {
        User user = userService.create(userRequestDto);
        session.setAttribute("username", user.getUsername());
        return new ResponseEntity<>(
                new UserResponseDto(user.getUsername()),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/auth/sign-in")
    public ResponseEntity<UserResponseDto> authorization(@Valid @RequestBody UserRequestDto userRequestDto,
                                  HttpSession session) {
        User user = userService.getUser(userRequestDto);
        session.setAttribute("username", user.getUsername());
        return new ResponseEntity<>(
                new UserResponseDto(user.getUsername()),
                HttpStatus.OK
        );
    }

    @PostMapping("/auth/sign-out")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
