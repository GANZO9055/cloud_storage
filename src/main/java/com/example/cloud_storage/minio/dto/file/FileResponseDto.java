package com.example.cloud_storage.minio.dto.file;

import com.example.cloud_storage.minio.dto.Resource;
import com.example.cloud_storage.minio.dto.Type;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Schema(description = "Файл")
public class FileResponseDto extends Resource {
    @Schema(description = "Путь до файла", example = "test1/test2/")
    private String path;
    @Schema(description = "Имя файла", example = "test3.txt")
    private String name;
    @Schema(description = "Размер файла", example = "42")
    private Long size;
    @Schema(description = "Тип ресурса", example = "FILE")
    private Type type;
}
