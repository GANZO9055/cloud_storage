package com.example.cloud_storage.exception.minio;

import com.example.cloud_storage.exception.CloudStorageException;

public class ParentFolderNotFoundException extends CloudStorageException {
    public ParentFolderNotFoundException(String message) {
        super(message);
    }
}
