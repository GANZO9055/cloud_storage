package com.example.cloud_storage.minio.validation;

import com.example.cloud_storage.minio.exception.InvalidPathException;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.regex.Pattern;

public class ValidationResource {

    private static final Pattern PATH_PATTERN = Pattern.compile("^[a-zA-Z0-9/_-]+/$");

    @Autowired
    private static MinioClient minioClient;

    public static boolean checkingExistenceResource(String bucket, String resourceName) {
        var resources = minioClient.listObjects(
                ListObjectsArgs.builder()
                .bucket(bucket)
                .prefix(resourceName)
                .maxKeys(1)
                .build()
        );
        return resources.iterator().hasNext();
    }

    public static void checkingPath(String path) {
        if (path.contains("..")) {
            throw new InvalidPathException("Path traversal is forbidden");
        }
        if (path.contains("//")) {
            throw new InvalidPathException("Invalid path format!");
        }
        if (!PATH_PATTERN.matcher(path).matches()) {
            throw new InvalidPathException("Invalid characters in path");
        }
    }
}
