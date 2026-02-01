package com.example.cloud_storage.exception.minio;

public class ParentFolderNotFoundException extends RuntimeException {
    public ParentFolderNotFoundException(String message) {
        super(message);
    }
}
