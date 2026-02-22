package com.example.cloud_storage.minio.controller;

import com.example.cloud_storage.minio.dto.Resource;
import com.example.cloud_storage.minio.service.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/api/resource")
@AllArgsConstructor
@Tag(name = "File", description = "Работа с файлами")
public class FileController {

    private ResourceService resourceService;

    @Operation(
            summary = "Получение ресурса"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешно"),
            @ApiResponse(responseCode = "400", description = "Невалидный или отсутствующий путь"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "404", description = "Ресурс не найден"),
            @ApiResponse(responseCode = "500", description = "Неизвестная ошибка")
    })
    @GetMapping
    public ResponseEntity<Resource> getResource(
            @RequestParam
            @Size(max = 500)
            @NotBlank
            String path) {
        return ResponseEntity.ok(resourceService.get(path));
    }

    @Operation(
            summary = "Удаление ресурса"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Ресурс удален"),
            @ApiResponse(responseCode = "400", description = "Невалидный или отсутствующий путь"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "404", description = "Ресурс не найден"),
            @ApiResponse(responseCode = "500", description = "Неизвестная ошибка")
    })
    @DeleteMapping
    public ResponseEntity<Void> deleteResource(
            @RequestParam
            @Size(max = 500)
            @NotBlank
            String path) {
        resourceService.delete(path);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Скачивание ресурса"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешно"),
            @ApiResponse(responseCode = "400", description = "Невалидный или отсутствующий путь"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "404", description = "Ресурс не найден"),
            @ApiResponse(responseCode = "500", description = "Неизвестная ошибка")
    })
    @GetMapping("/download")
    public ResponseEntity<StreamingResponseBody> downloadResource(
            @RequestParam
            @Size(max = 500)
            @NotBlank
            String path) {
        InputStream inputStream = resourceService.download(path);

        StreamingResponseBody stream = outputStream -> {
            try (inputStream) {
                inputStream.transferTo(outputStream);
            }
        };
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(stream);
    }

    @Operation(
            summary = "Переместить/переименовать ресурс"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешно"),
            @ApiResponse(responseCode = "400", description = "Невалидный или отсутствующий путь"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "404", description = "Ресурс не найден"),
            @ApiResponse(responseCode = "409", description = "Ресурс, лежащий на пути to уже существует"),
            @ApiResponse(responseCode = "500", description = "Неизвестная ошибка")
    })
    @PutMapping("/move")
    public ResponseEntity<Resource> moveResource(
            @RequestParam
            @Size(max = 500)
            @NotBlank String from,
            @RequestParam
            @Size(max = 500)
            @NotBlank String to) {
        return ResponseEntity.ok(resourceService.move(from, to));
    }

    @Operation(
            summary = "Поиск ресурсов"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешно"),
            @ApiResponse(responseCode = "400", description = "Невалидный или отсутствующий поисковой запрос"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "500", description = "Неизвестная ошибка")
    })
    @GetMapping("/search")
    public ResponseEntity<List<Resource>> searchResource(
            @RequestParam
            @Size(max = 500)
            @NotBlank
            String query) {
        return ResponseEntity.ok(resourceService.search(query));
    }

    @Operation(
            summary = "Загрузка ресурсов"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Ресурс загружен"),
            @ApiResponse(responseCode = "400", description = "Невалидное тело запроса"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "409", description = "Файл уже существует"),
            @ApiResponse(responseCode = "500", description = "Неизвестная ошибка")
    })
    @PostMapping
    public ResponseEntity<List<Resource>> uploadResource(
            @RequestParam("path")
            @Size(max = 500)
            @NotBlank String path,
            @RequestParam("object") List<MultipartFile> files) {
        return ResponseEntity.status(HttpStatus.CREATED).body(resourceService.upload(path, files));
    }
}
