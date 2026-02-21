package com.example.cloud_storage.minio.controller;

import com.example.cloud_storage.minio.dto.Resource;
import com.example.cloud_storage.minio.dto.directory.FolderResponseDto;
import com.example.cloud_storage.minio.service.directory.FolderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Validated
@RequestMapping("/api/directory")
@Tag(name = "Folder", description = "Операции с папками")
public class FolderController {

    private FolderService directoryService;

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
    public ResponseEntity<FolderResponseDto> createFolder(
            @RequestParam
            @Size(max = 500)
            @NotBlank
            String path) {
        return new ResponseEntity<>(
                directoryService.createFolder(path),
                HttpStatus.CREATED
        );
    }

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
    public ResponseEntity<List<Resource>> getFolderContents(
            @RequestParam
            @Size(max = 500)
            @NotBlank
            String path) {
        return new ResponseEntity<>(
                directoryService.getFolderContents(path),
                HttpStatus.OK
        );
    }
}
