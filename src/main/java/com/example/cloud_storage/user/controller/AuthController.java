package com.example.cloud_storage.user.controller;

import com.example.cloud_storage.user.model.User;
import com.example.cloud_storage.user.service.UserService;
import lombok.AllArgsConstructor;
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
    public void registration(@RequestBody User user) {
        userService.create(user);
    }

    @PostMapping("/auth/sign-in")
    public void authorization(@RequestBody User user) {
        userService.getUser(user);
    }

    @PostMapping("/auth/sign-out")
    public void logout() {

    }
}
