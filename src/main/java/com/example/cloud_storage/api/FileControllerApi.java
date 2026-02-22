package com.example.cloud_storage.api;

import com.example.cloud_storage.dto.Resource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;

@Tag(name = "File", description = "Работа с файлами")
public interface FileControllerApi {
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
    ResponseEntity<Resource> getResource(
            @RequestParam
            @Size(max = 500)
            @NotBlank
            String path);

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
    ResponseEntity<Void> deleteResource(
            @RequestParam
            @Size(max = 500)
            @NotBlank
            String path);

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
    ResponseEntity<StreamingResponseBody> downloadResource(
            @RequestParam
            @Size(max = 500)
            @NotBlank
            String path);

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
    ResponseEntity<Resource> moveResource(
            @RequestParam
            @Size(max = 500)
            @NotBlank String from,
            @RequestParam
            @Size(max = 500)
            @NotBlank String to);

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
    ResponseEntity<List<Resource>> searchResource(
            @RequestParam
            @Size(max = 500)
            @NotBlank
            String query);

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
    ResponseEntity<List<Resource>> uploadResource(
            @RequestParam("path")
            @Size(max = 500)
            @NotBlank String path,
            @RequestParam("object") List<MultipartFile> files);
}
