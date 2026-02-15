package com.example.cloud_storage.user.controller;

import com.example.cloud_storage.user.dto.UserResponseDto;
import com.example.cloud_storage.user.util.UserUtil;
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
@Tag(name = "User", description = "Операции с пользователями")
public class UserController {

    private UserUtil userUtil;

    @Operation(
            summary = "Получить текущего пользователя"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешно"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "500", description = "Неизвестная ошибка")
    })
    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getCurrentUser() {
        return new ResponseEntity<>(
                new UserResponseDto(userUtil.getUsername()),
                HttpStatus.OK
        );
    }
}
