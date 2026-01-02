package com.example.cloud_storage.minio.exception;

public class StorageException extends RuntimeException {
    public StorageException(String message) {
        super(message);
    }
}
