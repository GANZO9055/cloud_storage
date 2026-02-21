package com.example.cloud_storage.exception.minio;

import com.example.cloud_storage.exception.CloudStorageException;

public class ResourceAlreadyExistsException extends CloudStorageException {
    public ResourceAlreadyExistsException(String message) {
        super(message);
    }
}
