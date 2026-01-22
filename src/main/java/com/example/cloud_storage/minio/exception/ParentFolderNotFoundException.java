package com.example.cloud_storage.minio.exception;

public class ParentFolderNotFoundException extends RuntimeException {
    public ParentFolderNotFoundException(String message) {
        super(message);
    }
}
