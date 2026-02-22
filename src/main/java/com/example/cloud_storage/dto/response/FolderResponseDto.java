package com.example.cloud_storage.dto.response;

import com.example.cloud_storage.dto.Resource;
import com.example.cloud_storage.dto.Type;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class FolderResponseDto implements Resource {
    @Schema(description = "Путь до папки", example = "test1/test2/")
    @Size(max = 500)
    @NotBlank
    private String path;

    @Schema(description = "Имя папки", example = "test3/")
    @Size(max = 100)
    @NotBlank
    private String name;

    @Schema(description = "Тип ресурса", example = "FILE")
    @NotNull
    private Type type;
}
