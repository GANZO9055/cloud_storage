package com.example.cloud_storage.controller.user;

import com.example.cloud_storage.api.UserControllerApi;
import com.example.cloud_storage.dto.response.UserResponseDto;
import com.example.cloud_storage.util.UserUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController implements UserControllerApi {

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getCurrentUser() {
        return new ResponseEntity<>(
                new UserResponseDto(UserUtil.getUsername()),
                HttpStatus.OK
        );
    }
}
