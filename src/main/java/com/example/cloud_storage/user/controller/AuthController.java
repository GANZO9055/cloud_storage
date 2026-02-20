package com.example.cloud_storage.user.controller;

import com.example.cloud_storage.user.dto.UserRequestDto;
import com.example.cloud_storage.user.dto.UserResponseDto;
import com.example.cloud_storage.user.model.User;
import com.example.cloud_storage.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Регистрация и авторизация пользователей")
public class AuthController {

    private UserService userService;

    @Operation(
            summary = "Регистрация пользователей"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Пользователь зарегистрирован"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
            @ApiResponse(responseCode = "409", description = "Пользователь уже существует"),
            @ApiResponse(responseCode = "500", description = "Неизвестная ошибка")
    })
    @PostMapping("/sign-up")
    public ResponseEntity<UserResponseDto> registration(@Valid @RequestBody UserRequestDto userRequestDto,
                                                        HttpServletRequest request,
                                                        HttpServletResponse response) {
        UserResponseDto user = userService.create(userRequestDto, request, response);
        return new ResponseEntity<>(
                new UserResponseDto(user.getUsername()),
                HttpStatus.CREATED
        );
    }
    @Operation(
            summary = "Авторизация пользователя"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь авторизован"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
            @ApiResponse(responseCode = "401", description = "Неверные данные"),
            @ApiResponse(responseCode = "500", description = "Неизвестная ошибка")
    })
    @PostMapping("/sign-in")
    public ResponseEntity<UserResponseDto> authorization(@Valid @RequestBody UserRequestDto userRequestDto,
                                                         HttpServletRequest request,
                                                         HttpServletResponse response) {
        UserResponseDto user = userService.authenticate(userRequestDto, request, response);
        return new ResponseEntity<>(
                new UserResponseDto(user.getUsername()),
                HttpStatus.OK
        );
    }

    @Operation(
            summary = "Выход из аккаунта"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Пользователь вышел из аккаунта"),
            @ApiResponse(responseCode = "401", description = "Запрос исполняется неавторизованным пользователем"),
            @ApiResponse(responseCode = "500", description = "Неизвестная ошибка")
    })
    @PostMapping("/sign-out")
    public ResponseEntity<Void> logout() {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
