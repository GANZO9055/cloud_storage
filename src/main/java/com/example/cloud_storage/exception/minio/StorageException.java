package com.example.cloud_storage.exception.minio;

import com.example.cloud_storage.exception.CloudStorageException;

public class StorageException extends CloudStorageException {
    public StorageException(String message) {
        super(message);
    }
}
