package com.example.cloud_storage.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "minio")
public record MinioProperties(
        String url,
        @NotBlank String accessKey,
        @NotBlank String secretKey
) {

}
