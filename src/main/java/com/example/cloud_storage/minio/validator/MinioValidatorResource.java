package com.example.cloud_storage.minio.validator;

import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MinioValidatorResource {

    private MinioClient minioClient;

    public boolean checkingExistenceResource(String bucket, String path) {
        var resources = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucket)
                        .prefix(path)
                        .recursive(false)
                        .maxKeys(1)
                        .build()
        );
        return resources.iterator().hasNext();
    }
}
