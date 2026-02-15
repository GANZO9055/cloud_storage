package com.example.cloud_storage.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Schema(description = "Запрос пользователя")
public class UserRequestDto {

    @Schema(description = "Имя пользователя", example = "user_1")
    @NotBlank(message = "Username cannot be empty!")
    @Size(
            min = 3,
            max = 50,
            message = "Length of the user must be from 3 to 50 characters!"
    )
    private String username;

    @Schema(description = "Пароль", example = "password_1")
    @NotBlank(message = "Password cannot be empty!")
    @Size(
            min = 4,
            max = 100,
            message = "Length of the password must be from 3 to 100 characters!"
    )
    private String password;
}
