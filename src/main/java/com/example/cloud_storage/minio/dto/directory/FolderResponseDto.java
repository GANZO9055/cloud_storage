package com.example.cloud_storage.minio.dto.directory;

import com.example.cloud_storage.minio.dto.Resource;
import com.example.cloud_storage.minio.dto.Type;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class FolderResponseDto extends Resource {
    @Schema(description = "Путь до папки", example = "test1/test2/")
    private String path;
    @Schema(description = "Имя папки", example = "test3/")
    private String name;
    @Schema(description = "Тип ресурса", example = "FILE")
    private Type type;
}
