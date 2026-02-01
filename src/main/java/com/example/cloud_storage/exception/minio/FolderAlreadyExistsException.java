package com.example.cloud_storage.exception.minio;

public class FolderAlreadyExistsException extends RuntimeException {
    public FolderAlreadyExistsException(String message) {
        super(message);
    }
}
