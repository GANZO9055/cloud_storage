package com.example.cloud_storage.exception.minio;

import com.example.cloud_storage.exception.CloudStorageException;

public class FolderAlreadyExistsException extends CloudStorageException {
    public FolderAlreadyExistsException(String message) {
        super(message);
    }
}
