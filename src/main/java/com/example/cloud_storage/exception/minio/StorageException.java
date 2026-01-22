package com.example.cloud_storage.exception.minio;

public class StorageException extends RuntimeException {
    public StorageException(String message) {
        super(message);
    }
}
