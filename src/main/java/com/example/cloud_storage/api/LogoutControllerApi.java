package com.example.cloud_storage.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@Tag(name = "Logout", description = "Выход из аккаунта")
public interface LogoutControllerApi {
    @Operation(
            summary = "Выход из аккаунта"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Пользователь вышел из аккаунта"),
            @ApiResponse(responseCode = "401", description = "Запрос исполняется неавторизованным пользователем"),
            @ApiResponse(responseCode = "500", description = "Неизвестная ошибка")
    })
    @PostMapping("/sign-out")
    ResponseEntity<Void> logout();
}
