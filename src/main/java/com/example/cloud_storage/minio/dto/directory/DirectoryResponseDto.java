package com.example.cloud_storage.minio.dto.directory;

import com.example.cloud_storage.minio.dto.Resource;
import com.example.cloud_storage.minio.dto.Type;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class DirectoryResponseDto extends Resource {
    private String path;
    private String name;
    private Type type;
}
