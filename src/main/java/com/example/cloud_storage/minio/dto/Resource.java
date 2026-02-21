package com.example.cloud_storage.minio.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Основной ресурс")
public interface Resource {
    String getPath();
    String getName();
    String getType();
}
