package com.example.cloud_storage.exception.minio;

import com.example.cloud_storage.exception.CloudStorageException;

public class ResourceNotFoundException extends CloudStorageException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
