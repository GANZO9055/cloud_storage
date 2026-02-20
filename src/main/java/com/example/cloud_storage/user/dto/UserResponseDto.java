package com.example.cloud_storage.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Schema(description = "Ответ пользователя")
public class UserResponseDto {
    @Schema(description = "Имя пользователя", example = "user_1")
    @NotBlank(message = "Username cannot be null")
    @Size(max = 50)
    private String username;
}
