package com.example.cloud_storage.minio.validation;

import com.example.cloud_storage.exception.minio.InvalidPathException;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
@Slf4j
public class ValidationResource {

    private static final Pattern PATH_PATTERN = Pattern.compile("^[a-zA-Z0-9/_\\-.]+/?$");

    @Autowired
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

    public void checkingPath(String path) {
        if (!PATH_PATTERN.matcher(path).matches()) {
            log.warn("Invalid characters in path");
            throw new InvalidPathException("Invalid characters in path");
        }
    }
}
