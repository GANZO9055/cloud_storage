package com.example.cloud_storage.minio.dto.file;

import com.example.cloud_storage.minio.dto.Resource;
import com.example.cloud_storage.minio.dto.Type;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Schema(description = "Файл")
public class FileResponseDto implements Resource {
    @Schema(description = "Путь до файла", example = "test1/test2/")
    @Size(max = 500)
    @NotBlank
    private String path;

    @Schema(description = "Имя файла", example = "test3.txt")
    @Size(max = 100)
    @NotBlank
    private String name;

    @Schema(description = "Размер файла", example = "42")
    @Min(0)
    private Long size;

    @Schema(description = "Тип ресурса", example = "FILE")
    @NotNull
    private Type type;
}
