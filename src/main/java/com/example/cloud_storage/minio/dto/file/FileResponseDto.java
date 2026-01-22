package com.example.cloud_storage.minio.dto.file;

import com.example.cloud_storage.minio.dto.Resource;
import com.example.cloud_storage.minio.dto.Type;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class FileResponseDto extends Resource {
    private String path;
    private String name;
    private Long size;
    private Type type;
}
