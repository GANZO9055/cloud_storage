package com.example.cloud_storage.exception.user;

import com.example.cloud_storage.exception.CloudStorageException;

public class UnauthorizedUserException extends CloudStorageException {
    public UnauthorizedUserException(String message) {
        super(message);
    }
}
