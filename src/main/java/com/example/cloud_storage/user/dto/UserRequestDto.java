package com.example.cloud_storage.user.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class UserRequestDto {
    @NotBlank(message = "Username cannot be empty!")
    @Size(
            min = 3,
            max = 50,
            message = "Length of the user must be from 3 to 50 characters!"
    )
    private String username;
    @NotBlank(message = "Password cannot be empty!")
    @Size(
            min = 4,
            max = 100,
            message = "Length of the password must be from 3 to 100 characters!"
    )
    private String password;
}
