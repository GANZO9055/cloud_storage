package com.example.cloud_storage.exception.minio;

import com.example.cloud_storage.exception.CloudStorageException;

public class FolderNotFoundException extends CloudStorageException {
    public FolderNotFoundException(String message) {
        super(message);
    }
}
