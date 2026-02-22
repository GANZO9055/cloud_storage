package com.example.cloud_storage.api;

import com.example.cloud_storage.dto.Resource;
import com.example.cloud_storage.dto.response.FolderResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Folder", description = "Операции с папками")
public interface FolderControllerApi {
    @Operation(
            summary = "Создать папку"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Папка создана"),
            @ApiResponse(responseCode = "400", description = "Невалидный или отсутствующий путь к новой папке"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "404", description = "Родительская папка не существует"),
            @ApiResponse(responseCode = "409", description = "Папка уже существует"),
            @ApiResponse(responseCode = "500", description = "Неизвестная ошибка")
    })
    @PostMapping
    ResponseEntity<FolderResponseDto> createFolder(
            @RequestParam
            @Size(max = 500)
            @NotBlank
            String path);

    @Operation(
            summary = "Получить содержимое папки"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешно"),
            @ApiResponse(responseCode = "400", description = "Невалидный или отсутствующий путь"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "404", description = "Папка не существует"),
            @ApiResponse(responseCode = "500", description = "Неизвестная ошибка")
    })
    @GetMapping
    ResponseEntity<List<Resource>> getFolderContents(
            @RequestParam
            @Size(max = 500)
            @NotBlank
            String path);
}
