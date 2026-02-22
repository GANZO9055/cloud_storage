package com.example.cloud_storage.api;

import com.example.cloud_storage.dto.request.UserRequestDto;
import com.example.cloud_storage.dto.response.UserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Auth", description = "Регистрация и авторизация пользователей")
public interface AuthControllerApi {

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
    ResponseEntity<UserResponseDto> registration(@Valid @RequestBody UserRequestDto userRequestDto,
                                                        HttpServletRequest request,
                                                        HttpServletResponse response);

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
    ResponseEntity<UserResponseDto> authorization(@Valid @RequestBody UserRequestDto userRequestDto,
                                                         HttpServletRequest request,
                                                         HttpServletResponse response);
}
